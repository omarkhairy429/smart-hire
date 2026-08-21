import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Posting, PostingRequest } from '../models/posting.model';

@Injectable({ providedIn: 'root' })
export class PostingsService {
    private readonly http = inject(HttpClient);
    private readonly endpoint = 'http://localhost:8080/api/postings';

    getPostings(): Observable<Posting[]> {
        return this.http.get<Posting[]>(this.endpoint);
    }

    createPosting(request: PostingRequest): Observable<Posting> {
        return this.http.post<Posting>(this.endpoint, request);
    }
}