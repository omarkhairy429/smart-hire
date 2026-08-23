import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { StaffResponse } from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private apiUrl = `${environment.apiUrl}/super-admin/staff`;

  constructor(private http: HttpClient) {}

  getStaff(): Observable<StaffResponse[]> {
    return this.http.get<StaffResponse[]>(this.apiUrl);
  }

  createStaff(staffData: any): Observable<any> {
    return this.http.post(this.apiUrl, staffData);
  }
}
