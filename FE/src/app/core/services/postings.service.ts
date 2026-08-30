import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { PostingResponse } from '../models/api.models';

export interface PostingRequest {
  title: string;
  company: string;
  description: string;
  skillsRequired: string[];
  locationType: string;
  location: string;
  deadline: string;
}

@Injectable({
  providedIn: 'root',
})
export class PostingService {
  private apiUrl = `${environment.apiUrl}/postings`;

  constructor(private http: HttpClient) {}

  getPostings(): Observable<PostingResponse[]> {
    return this.http.get<any>(this.apiUrl).pipe(
      tap((res: any) => console.log('[PostingService] getPostings raw response:', res)),
      map((res: any) => (Array.isArray(res) ? res : (res?.data ?? res))),
    );
  }

  getPostingById(id: string): Observable<PostingResponse> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      tap((res: any) => console.log('[PostingService] getPostingById raw response:', res)),
      map((res: any) => (res?.id ? res : (res?.data ?? res))),
    );
  }

  createPosting(request: PostingRequest): Observable<PostingResponse> {
    return this.http
      .post<any>(this.apiUrl, request)
      .pipe(map((res: any) => (res?.id ? res : (res?.data ?? res))));
  }
  getPublishedPostings(): Observable<PostingResponse[]> {
    return this.http
      .get<any>(`${this.apiUrl}/published`)
      .pipe(map((res: any) => (Array.isArray(res) ? res : (res?.data ?? res))));
  }

  searchPublicPostings(filters: {
    keyword?: string;
    location?: string;
    locationType?: string;
    company?: string;
  }): Observable<PostingResponse[]> {
    let params = new HttpParams();
    if (filters.keyword) params = params.set('keyword', filters.keyword);
    if (filters.location) params = params.set('location', filters.location);
    if (filters.locationType) params = params.set('locationType', filters.locationType);
    if (filters.company) params = params.set('company', filters.company);

    return this.http
      .get<any>(`${environment.apiUrl}/public/postings`, { params })
      .pipe(map((res: any) => (Array.isArray(res) ? res : (res?.data ?? res))));
  }

  getMyPostings(): Observable<PostingResponse[]> {
    return this.http
      .get<any>(`${this.apiUrl}/mine`)
      .pipe(map((res: any) => (Array.isArray(res) ? res : (res?.data ?? res))));
  }

  createDraft(request: PostingRequest): Observable<PostingResponse> {
    return this.http
      .post<any>(`${this.apiUrl}/drafts`, request)
      .pipe(map((res: any) => (res?.id ? res : (res?.data ?? res))));
  }

  updateDraft(id: string, request: PostingRequest): Observable<PostingResponse> {
    return this.http
      .put<any>(`${this.apiUrl}/drafts/${id}`, request)
      .pipe(map((res: any) => (res?.id ? res : (res?.data ?? res))));
  }

  publishPosting(id: string): Observable<PostingResponse> {
    return this.http
      .patch<any>(`${this.apiUrl}/${id}/publish`, {})
      .pipe(map((res: any) => (res?.id ? res : (res?.data ?? res))));
  }

  closePosting(id: string): Observable<PostingResponse> {
    return this.http
      .patch<any>(`${this.apiUrl}/${id}/close`, {})
      .pipe(map((res: any) => (res?.id ? res : (res?.data ?? res))));
  }
  deletePosting(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
  getPublishedPostingById(id: string): Observable<PostingResponse> {
    return this.http
      .get<any>(`${this.apiUrl}/published/${id}`)
      .pipe(map((res: any) => (res?.id ? res : (res?.data ?? res))));
  }
}
