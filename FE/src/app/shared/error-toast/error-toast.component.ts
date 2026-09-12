import { Component } from '@angular/core';
import { AsyncPipe, NgIf } from '@angular/common';
import { Observable } from 'rxjs';
import { ErrorService } from '../../core/services/error.service';

@Component({
  selector: 'app-error-toast',
  standalone: true,
  imports: [AsyncPipe, NgIf],
  templateUrl: './error-toast.component.html',
})
export class ErrorToastComponent {
  error$: Observable<string | null>;

  constructor(private errorService: ErrorService) {
    this.error$ = errorService.error$;
  }

  dismiss(): void {
    this.errorService.clear();
  }
}
