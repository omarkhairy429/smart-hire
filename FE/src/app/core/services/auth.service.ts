import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface RegisterData {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  role: string;
  email: string;
  firstName: string;
}

export interface CurrentUser {
  token: string;
  role: string;
  email: string;
  firstName: string;
  sub?: string; // UUID from JWT sub claim (used as hrManagerId)
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: LoginCredentials): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap((res) => {
        localStorage.setItem('token', res.token);
        // Decode JWT payload to extract `sub` (user UUID)
        const decoded = this.decodeToken(res.token);
        const user: CurrentUser = {
          token: res.token,
          role: res.role,
          email: res.email,
          firstName: res.firstName,
          sub: decoded?.sub,
        };
        localStorage.setItem('user', JSON.stringify(user));
      })
    );
  }

  register(userData: RegisterData): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, userData);
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.router.navigate(['/']);
  }

  getCurrentUser(): CurrentUser | null {
    const userStr = localStorage.getItem('user');
    if (!userStr) return null;
    try {
      return JSON.parse(userStr);
    } catch {
      return null;
    }
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  getRole(): string | null {
    return this.getCurrentUser()?.role ?? null;
  }

  /** Decodes a JWT and returns the payload as a plain object. */
  private decodeToken(token: string): any {
    try {
      const payload = token.split('.')[1];
      const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
      return JSON.parse(decoded);
    } catch {
      return null;
    }
  }
}
