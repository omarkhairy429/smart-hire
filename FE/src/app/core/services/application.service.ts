import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { ApplicationResponse, ApplyRequest, ApplicationStage, PipelineResponse } from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class ApplicationService {
  private apiUrl = `${environment.apiUrl}/applications`;
  private postingsUrl = `${environment.apiUrl}/postings`;

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


  getApplicationsForPosting(
    postingId: string,
    filters: { stage?: ApplicationStage | ''; sort?: string; dir?: string },
  ): Observable<ApplicationResponse[]> {
    let params = new HttpParams()
      .set('sort', filters.sort || 'createdAt')
      .set('dir', filters.dir || 'asc');
    if (filters.stage) params = params.set('stage', filters.stage);

    return this.http
      .get<any>(`${this.postingsUrl}/${postingId}/applications`, { params })
      .pipe(map((res: any) => (Array.isArray(res) ? res : (res?.data ?? res))));
  }


  exportApplicationsCsv(
    postingId: string,
    filters: { stage?: ApplicationStage | ''; sort?: string; dir?: string },
  ): Observable<Blob> {
    let params = new HttpParams()
      .set('sort', filters.sort || 'createdAt')
      .set('dir', filters.dir || 'asc');
    if (filters.stage) params = params.set('stage', filters.stage);

    return this.http.get(`${this.postingsUrl}/${postingId}/applications/export`, {
      params,
      responseType: 'blob',
    });
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
