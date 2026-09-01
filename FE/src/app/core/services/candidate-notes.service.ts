import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { CandidateNoteRequest, CandidateNoteResponse } from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class CandidateNotesService {
  private apiUrl = `${environment.apiUrl}/hr/notes`;

  constructor(private http: HttpClient) {}

  /** Fetch all notes for a given candidate (HR only). */
  getNotesByCandidate(candidateId: string): Observable<CandidateNoteResponse[]> {
    return this.http
      .get<any>(`${this.apiUrl}/candidate/${candidateId}`)
      .pipe(map((res: any) => (Array.isArray(res) ? res : (res?.data ?? []))));
  }

  /** Post a new note for a candidate (HR only). */
  createNote(req: CandidateNoteRequest): Observable<CandidateNoteResponse> {
    return this.http
      .post<any>(this.apiUrl, req)
      .pipe(map((res: any) => (res?.id ? res : (res?.data ?? res))));
  }
}
