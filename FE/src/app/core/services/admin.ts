import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { StaffResponse } from '../models/api.models';

export interface CreateStaffRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  role: string;
  companyName: string;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  private apiUrl = `${environment.apiUrl}/super-admin`;

  constructor(private http: HttpClient) {}

  getStaff(): Observable<StaffResponse[]> {
    return this.http.get<StaffResponse[]>(`${this.apiUrl}/staff`);
  }

  createStaff(staffData: CreateStaffRequest): Observable<StaffResponse> {
    return this.http.post<StaffResponse>(`${this.apiUrl}/staff`, staffData);
  }
}
