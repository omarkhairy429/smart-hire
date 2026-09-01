import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { catchError, of } from 'rxjs';
import { ApplicationService } from '../../../core/services/application.service';
import { InterviewService } from '../../../core/services/interview.service';
import { ApplicationResponse, ApplicationStage, InterviewResponse } from '../../../core/models/api.models';

@Component({
  selector: 'app-my-applications',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './my-applications.component.html',
  styleUrls: ['./my-applications.component.css']
})
export class MyApplicationsComponent implements OnInit {
  applications: ApplicationResponse[] = [];
  /** Map of applicationId → interview details */
  interviewMap: Record<string, InterviewResponse | null> = {};
  isLoading = true;
  errorMessage = '';

  constructor(
    private applicationService: ApplicationService,
    private interviewService: InterviewService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    forkJoin({
      applications: this.applicationService.getMyApplications(),
      interviews: this.interviewService.getMyInterviewsAsCandidate().pipe(
        catchError((err) => {
          console.warn('[MyApplications] Could not load candidate interviews:', err);
          return of([] as InterviewResponse[]);
        })
      )
    }).subscribe({
      next: ({ applications, interviews }) => {
        this.applications = applications;
        // Build a map of applicationId → interview for O(1) lookup in the template
        interviews.forEach(iv => {
          this.interviewMap[iv.applicationId] = iv;
        });
        // Mark applications with INTERVIEW stage that have no loaded interview as null (not undefined)
        applications
          .filter(a => a.stage === ApplicationStage.INTERVIEW)
          .forEach(a => {
            if (!(a.id in this.interviewMap)) {
              this.interviewMap[a.id] = null;
            }
          });
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.errorMessage = 'Could not load your applications.';
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleDateString('en-US', {
      year: 'numeric', month: 'short', day: 'numeric'
    });
  }

  formatDateTime(value: string): string {
    if (!value) return '—';
    return new Date(value).toLocaleString('en-US', {
      year: 'numeric', month: 'short', day: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  }

  statusClass(status: string): string {
    switch (status?.toUpperCase()) {
      case 'ACCEPTED': return 'status-accepted';
      case 'REJECTED': return 'status-rejected';
      case 'PENDING': return 'status-pending';
      default: return 'status-pending';
    }
  }
}
