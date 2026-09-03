import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PostingService } from '../../../core/services/postings.service';
import { ApplicationService } from '../../../core/services/application.service';
import { CandidateNotesService } from '../../../core/services/candidate-notes.service';
import {
  PostingResponse,
  ApplicationResponse,
  ApplicationStage,
  CandidateNoteResponse,
} from '../../../core/models/api.models';
import { HrScheduleInterviewComponent } from '../hr-schedule-interview/hr-schedule-interview.component';

@Component({
  selector: 'app-hr-applications',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, HrScheduleInterviewComponent],
  templateUrl: './hr-applications.component.html',
  styleUrls: ['./hr-applications.component.css'],
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
    ApplicationStage.REJECTED,
  ];

  schedulingFor: ApplicationResponse | null = null;

  isLoadingPostings = true;
  isLoadingApps = false;
  errorMessage = '';

  // --- Notes panel state ---
  notesPanelOpen = false;
  selectedCandidateId: string = '';
  selectedCandidateName: string = '';
  notesList: CandidateNoteResponse[] = [];
  newNoteContent: string = '';
  isLoadingNotes = false;
  isSavingNote = false;
  notesError = '';

  constructor(
    private postingService: PostingService,
    private applicationService: ApplicationService,
    private notesService: CandidateNotesService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.loadPostings();
  }

loadPostings() {
    this.postingService.getPostingsByCompany().subscribe({
      next: (data) => {
        this.postings = data;
        this.isLoadingPostings = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.errorMessage = 'Could not load postings.';
        this.isLoadingPostings = false;
        this.cdr.markForCheck();
      },
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
      error: () => {
        this.errorMessage = 'Could not load applications for this posting.';
        this.isLoadingApps = false;
        this.cdr.markForCheck();
      },
    });
  }

  // --- Notes panel methods ---

  openNotesPanel(app: ApplicationResponse) {
    this.selectedCandidateId = app.candidateId;
    this.selectedCandidateName = app.candidateName || 'Unknown Candidate';
    this.newNoteContent = '';
    this.notesError = '';
    this.notesPanelOpen = true;
    this.loadNotes();
  }

  closeNotesPanel() {
    this.notesPanelOpen = false;
    this.notesList = [];
  }

  loadNotes() {
    this.isLoadingNotes = true;
    this.notesError = '';
    this.notesService.getNotesByCandidate(this.selectedCandidateId).subscribe({
      next: (notes) => {
        // Newest first
        this.notesList = notes.sort(
          (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
        );
        this.isLoadingNotes = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.notesError = 'Could not load notes.';
        this.isLoadingNotes = false;
        this.cdr.markForCheck();
      },
    });
  }

  submitNote() {
    const content = this.newNoteContent.trim();
    if (!content || this.isSavingNote) return;
    this.isSavingNote = true;
    this.notesError = '';

    this.notesService.createNote({ candidateId: this.selectedCandidateId, content }).subscribe({
      next: (note) => {
        this.notesList.unshift(note); // Prepend so newest is at top
        this.newNoteContent = '';
        this.isSavingNote = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.notesError = 'Could not save note. Please try again.';
        this.isSavingNote = false;
        this.cdr.markForCheck();
      },
    });
  }

  onStageChange(app: ApplicationResponse, event: Event): void {
    const select = event.target as HTMLSelectElement;
    const newStage = select.value as ApplicationStage;

    if (app.stage === newStage) {
      return;
    }

    this.applicationService.updateApplicationStage(app.id, newStage).subscribe({
      next: () => {
        app.stage = newStage;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Failed to update application stage:', err);
        this.errorMessage = 'Could not update application stage.';
        this.cdr.markForCheck();
      },
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
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  }

  formatDateTime(dateStr: string): string {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }
}