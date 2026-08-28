import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { ApplicationResponse, ApplyRequest, ApplicationStage, PipelineResponse } from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class ApplicationService {
  private apiUrl = `${environment.apiUrl}/applications`;

  constructor(private http: HttpClient) { }

  applyToPosting(req: ApplyRequest): Observable<ApplicationResponse> {
    return this.http.post<any>(this.apiUrl, req).pipe(
      tap((res) => console.log('[ApplicationService] apply response:', res)),
      map((res: any) => (res?.id ? res : (res?.data ?? res)))
    );
  }

  /** Returns the logged-in candidate's own applications. */
  getMyApplications(): Observable<ApplicationResponse[]> {
    return this.http.get<any>(this.apiUrl).pipe(
      tap((res) => console.log('[ApplicationService] my applications:', res)),
      map((res: any) => (Array.isArray(res) ? res : (res?.data ?? res)))
    );
  }

  /** Returns all applications for a given posting (HR/Admin only). */
  getApplicationsByPosting(postingId: string): Observable<ApplicationResponse[]> {
    return this.http.get<any>(`${this.apiUrl}/posting/${postingId}`).pipe(
      map((res: any) => (Array.isArray(res) ? res : (res?.data ?? res)))
    );
  }

  getPipeline(postingId: string): Observable<PipelineResponse[]> {
    return this.http.get<PipelineResponse[]>(`${environment.apiUrl}/postings/${postingId}/pipeline`);
  }

  updateApplicationStage(applicationId: string, stage: ApplicationStage):
    Observable<ApplicationResponse> {
    return this.http.patch<ApplicationResponse>(
      `${this.apiUrl}/${applicationId}/stage`,
      { stage }
    )
  }
}
