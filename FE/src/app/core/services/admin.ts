import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  StaffResponse,
  PlatformStatsResponse,
  AuditLogResponse,
  PageResponse,
} from '../models/api.models';

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
  deactivateStaff(id: string): Observable<StaffResponse> {
    return this.http.patch<StaffResponse>(`${this.apiUrl}/staff/${id}/deactivate`, {});
  }

  reactivateStaff(id: string): Observable<StaffResponse> {
    return this.http.patch<StaffResponse>(`${this.apiUrl}/staff/${id}/reactivate`, {});
  }

  getStats(): Observable<PlatformStatsResponse> {
    return this.http.get<PlatformStatsResponse>(`${this.apiUrl}/stats`);
  }

  getAuditLogs(
    page: number = 0,
    size: number = 20,
    action?: string,
  ): Observable<PageResponse<AuditLogResponse>> {
    let params = new HttpParams().set('page', page).set('size', size);

    if (action) {
      params = params.set('action', action);
    }

    return this.http.get<PageResponse<AuditLogResponse>>(`${this.apiUrl}/audit-logs`, { params });
  }
}
