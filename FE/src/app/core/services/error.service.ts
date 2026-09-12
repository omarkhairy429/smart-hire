import { Injectable, NgZone } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ErrorService {
  private errorSubject = new BehaviorSubject<string | null>(null);
  error$ = this.errorSubject.asObservable();

  constructor(private ngZone: NgZone) {}

  show(message: string): void {
    this.ngZone.run(() => {
      this.errorSubject.next(message);
    });
    setTimeout(() => this.clear(), 5000);
  }

  clear(): void {
    this.ngZone.run(() => {
      this.errorSubject.next(null);
    });
  }
}
