import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { InterviewService } from '../../../core/services/interview.service';
import { InterviewResponse } from '../../../core/models/interview.models';

@Component({
  selector: 'app-my-interviews',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './my-interviews.component.html',
  styleUrls: ['./my-interviews.component.css']
})
export class MyInterviewsComponent implements OnInit {
  interviews: InterviewResponse[] = [];
  isLoading = true;
  errorMessage = '';

  constructor(
    private interviewService: InterviewService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.interviewService.getMyInterviews().subscribe({
      next: (data) => {
        this.interviews = data;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.errorMessage = 'Could not load your interviews.';
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  formatDateTime(value: string): string {
    if (!value) return '—';
    return new Date(value).toLocaleString('en-US', {
      year: 'numeric', month: 'short', day: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  }
}
