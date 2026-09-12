import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ErrorService {
  private errorSubject = new BehaviorSubject<string | null>(null);
  error$ = this.errorSubject.asObservable();

  show(message: string): void {
    this.errorSubject.next(message);
    setTimeout(() => this.clear(), 5000);
  }

  clear(): void {
    this.errorSubject.next(null);
  }
}
