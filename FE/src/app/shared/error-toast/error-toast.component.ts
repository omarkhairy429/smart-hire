import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { ErrorService } from '../../core/services/error.service';

@Component({
  selector: 'app-error-toast',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './error-toast.component.html',
})
export class ErrorToastComponent implements OnInit, OnDestroy {
  message: string | null = null;
  private sub!: Subscription;

  constructor(private errorService: ErrorService) {}

  ngOnInit(): void {
    this.sub = this.errorService.error$.subscribe(msg => {
      this.message = msg;
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  dismiss(): void {
    this.errorService.clear();
  }
}
