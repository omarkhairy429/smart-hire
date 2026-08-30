import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PostingService } from '../../../core/services/postings.service';
import { ApplicationService } from '../../../core/services/application.service';
import { PostingResponse, ApplicationResponse, ApplicationStage } from '../../../core/models/api.models';
import { HrScheduleInterviewComponent } from '../hr-schedule-interview/hr-schedule-interview.component';

@Component({
  selector: 'app-hr-applications',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, HrScheduleInterviewComponent],
  templateUrl: './hr-applications.component.html',
  styleUrls: ['./hr-applications.component.css']
})
export class HrApplicationsComponent implements OnInit {
  postings: PostingResponse[] = [];
  applications: ApplicationResponse[] = [];
  selectedPostingId: string = '';
  selectedPostingTitle: string = '';

  stages: ApplicationStage[] = [
    ApplicationStage.APPLIED,
    ApplicationStage.SCREENING,
    ApplicationStage.INTERVIEW,
    ApplicationStage.OFFERED,
    ApplicationStage.REJECTED
  ];

  schedulingFor: ApplicationResponse | null = null;

  isLoadingPostings = true;
  isLoadingApps = false;
  errorMessage = '';

  constructor(
    private postingService: PostingService,
    private applicationService: ApplicationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadPostings();
  }

  loadPostings() {
    this.postingService.getPostings().subscribe({
      next: (data) => {
        // Show all postings — the backend already scopes by the HR's token
        this.postings = data;
        this.isLoadingPostings = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.errorMessage = 'Could not load postings.';
        this.isLoadingPostings = false;
        this.cdr.markForCheck();
      }
    });
  }

  onPostingSelect() {
    if (!this.selectedPostingId) {
      this.applications = [];
      return;
    }
    const posting = this.postings.find((p) => p.id === this.selectedPostingId);
    this.selectedPostingTitle = posting?.title ?? '';
    this.isLoadingApps = true;
    this.applications = [];
    this.errorMessage = '';

    this.applicationService.getApplicationsByPosting(this.selectedPostingId).subscribe({
      next: (apps) => {
        this.applications = apps;
        this.isLoadingApps = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.errorMessage = 'Could not load applications for this posting.';
        this.isLoadingApps = false;
        this.cdr.markForCheck();
      }
    });
  }

  onStageChange(app: ApplicationResponse, event: Event): void {
    const select = event.target as HTMLSelectElement;
    const newStage = select.value as ApplicationStage;

    if (app.stage === newStage) {
      return;
    }

    this.applicationService
      .updateApplicationStage(app.id, newStage)
      .subscribe({
        next: () => {
          app.stage = newStage;
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error('Failed to update application stage:', err);
          this.errorMessage = 'Could not update application stage.';
          this.cdr.markForCheck();
        }
      });
  }

  openSchedule(app: ApplicationResponse): void {
    this.schedulingFor = app;
  }

  onScheduled(): void {
    // Reload so the Stage column reflects the move to INTERVIEW
    this.onPostingSelect();
  }

  closeSchedule(): void {
    this.schedulingFor = null;
    this.cdr.markForCheck();
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleDateString('en-US', {
      year: 'numeric', month: 'short', day: 'numeric'
    });
  }
}
