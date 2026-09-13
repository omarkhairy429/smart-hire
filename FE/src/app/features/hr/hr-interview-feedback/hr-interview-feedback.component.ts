import { Component, EventEmitter, Input, OnInit, Output, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { InterviewService } from '../../../core/services/interview.service';
import { FeedbackResponse, InterviewResponse } from '../../../core/models/api.models';

interface InterviewWithFeedback {
  interview: InterviewResponse;
  feedback: FeedbackResponse[];
}

@Component({
  selector: 'app-hr-interview-feedback',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './hr-interview-feedback.component.html',
  styleUrls: ['./hr-interview-feedback.component.css']
})
export class HrInterviewFeedbackComponent implements OnInit {
  @Input({ required: true }) applicationId!: string;
  @Input() candidateName = '';

  @Output() closed = new EventEmitter<void>();

  rows: InterviewWithFeedback[] = [];
  isLoading = true;
  errorMessage = '';

  constructor(
    private interviewService: InterviewService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.interviewService.getInterviewsByApplication(this.applicationId).subscribe({
      next: (interviews) => {
        if (interviews.length === 0) {
          this.isLoading = false;
          this.cdr.markForCheck();
          return;
        }

        // Feedback is fetched per interview, so load them all before rendering
        forkJoin(
          interviews.map((interview) =>
            this.interviewService.getFeedbackForInterview(interview.id).pipe(
              catchError(() => of([] as FeedbackResponse[])),
              map((feedback) => ({ interview, feedback }))
            )
          )
        ).subscribe({
          next: (rows) => {
            this.rows = rows;
            this.isLoading = false;
            this.cdr.markForCheck();
          },
          error: () => {
            this.errorMessage = 'Could not load feedback.';
            this.isLoading = false;
            this.cdr.markForCheck();
          }
        });
      },
      error: () => {
        this.errorMessage = 'Could not load interviews for this application.';
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  close() {
    this.closed.emit();
  }

  formatDateTime(value: string): string {
    if (!value) return '—';
    return new Date(value).toLocaleString('en-US', {
      year: 'numeric', month: 'short', day: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  }

  recommendationClass(recommendation: string): string {
    switch (recommendation) {
      case 'PROCEED': return 'rec-proceed';
      case 'REJECT': return 'rec-reject';
      default: return 'rec-hold';
    }
  }
}
