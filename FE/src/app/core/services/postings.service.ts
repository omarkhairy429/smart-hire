import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PostingResponse } from '../models/api.models';

@Injectable({
  providedIn: 'root'
})
export class PostingService {
  private apiUrl = 'http://localhost:8080/api/postings';

  constructor(private http: HttpClient) {}

  getPostings(): Observable<PostingResponse[]> {
    return this.http.get<PostingResponse[]>(this.apiUrl);
  }

  getPostingById(id: string): Observable<PostingResponse> {
    return this.http.get<PostingResponse>(`${this.apiUrl}/${id}`);
  }
}