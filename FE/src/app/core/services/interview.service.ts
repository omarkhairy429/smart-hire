import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  DossierResponse,
  InterviewResponse,
  ScheduleInterviewRequest,
  StaffResponse
} from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class InterviewService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  /** HR: staff who can be assigned to an interview. */
  getInterviewers(): Observable<StaffResponse[]> {
    return this.http.get<StaffResponse[]>(`${this.apiUrl}/users/interviewers`);
  }

  scheduleInterview(
    applicationId: string,
    req: ScheduleInterviewRequest
  ): Observable<InterviewResponse> {
    return this.http.post<InterviewResponse>(
      `${this.apiUrl}/applications/${applicationId}/interviews`,
      req
    );
  }

  getInterviewsByApplication(applicationId: string): Observable<InterviewResponse[]> {
    return this.http.get<InterviewResponse[]>(
      `${this.apiUrl}/applications/${applicationId}/interviews`
    );
  }

  cancelInterview(interviewId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/interviews/${interviewId}`);
  }

  getMyInterviews(): Observable<InterviewResponse[]> {
    return this.http.get<InterviewResponse[]>(`${this.apiUrl}/interviewer/my-interviews`);
  }

  getDossier(interviewId: string): Observable<DossierResponse> {
    return this.http.get<DossierResponse>(
      `${this.apiUrl}/interviewer/interviews/${interviewId}/dossier`
    );
  }
}
