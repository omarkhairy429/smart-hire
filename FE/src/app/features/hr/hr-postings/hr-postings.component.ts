import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PostingService, PostingRequest } from '../../../core/services/postings.service';
import { AuthService } from '../../../core/services/auth.service';
import { PostingResponse } from '../../../core/models/api.models';

const UUID_REGEX = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;

@Component({
  selector: 'app-hr-postings',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule, FormsModule],
  templateUrl: './hr-postings.component.html',
  styleUrls: ['./hr-postings.component.css'],
})
export class HrPostingsComponent implements OnInit {
  postings: PostingResponse[] = [];
  isLoading = true;
  isSubmitting = false;
  successMessage = '';
  errorMessage = '';
  hrManagerId: string = '';
  hasAutoId = false;
  statusFilter: string = 'ALL';
  keywordFilter: string = '';
  locationTypeFilter: string = '';
  editingDraftId: string | null = null;
  readonly statusFilters = ['ALL', 'DRAFT', 'PUBLISHED', 'CLOSED'];
  readonly locationTypes = ['REMOTE', 'HYBRID', 'ON_SITE'];
  readonly employmentTypes = ['FULL_TIME', 'PART_TIME', 'CONTRACT', 'INTERNSHIP', 'FREELANCE'];

  postingForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private postingService: PostingService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
  ) {
    this.postingForm = this.fb.group({
      hrManagerId: ['', [Validators.required, Validators.pattern(UUID_REGEX)]],
      title: ['', [Validators.required, Validators.maxLength(120)]],
      company: ['', [Validators.required, Validators.maxLength(120)]],
      description: ['', [Validators.required, Validators.maxLength(3000)]],
      skillsRequired: ['', Validators.required],
      locationType: ['REMOTE', Validators.required],
      location: ['', Validators.maxLength(160)],
      deadline: ['', Validators.required],
      department: ['', Validators.maxLength(120)],
      employmentType: [''],
    });
  }

  ngOnInit() {
    // Auto-fill hrManagerId from JWT sub if it's a valid UUID
    const user = this.authService.getCurrentUser();
    if (user?.sub && UUID_REGEX.test(user.sub)) {
      this.hrManagerId = user.sub;
      this.hasAutoId = true;
      this.postingForm.patchValue({ hrManagerId: user.sub });
    }
    this.loadPostings();
  }

  loadPostings() {
    this.isLoading = true;
    this.postingService.getMyPostings().subscribe({
      next: (data) => {
        // Show all postings returned by the BE (server already scopes by role)
        this.postings = data;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.errorMessage = 'Could not load postings.';
        this.isLoading = false;
        this.cdr.markForCheck();
      },
    });
  }

  createPosting() {
    this.successMessage = '';
    this.errorMessage = '';

    if (this.postingForm.invalid) {
      this.postingForm.markAllAsTouched();
      return;
    }

    const raw = this.postingForm.getRawValue();
    const request: PostingRequest = {
      ...raw,
      skillsRequired: raw.skillsRequired
        .split(',')
        .map((s: string) => s.trim())
        .filter(Boolean),
    };

    this.isSubmitting = true;

    if (this.editingDraftId) {
      const draftId = this.editingDraftId;
      this.postingService.updateDraft(draftId, request).subscribe({
        next: () => {
          this.postingService.publishPosting(draftId).subscribe({
            next: () => {
              this.successMessage = 'Draft published.';
              this.editingDraftId = null;
              this.postingForm.reset({ locationType: 'REMOTE', hrManagerId: this.hrManagerId });
              this.isSubmitting = false;
              this.loadPostings();
            },
            error: (err) => {
              this.errorMessage = err?.error?.message ?? 'Failed to publish draft.';
              this.isSubmitting = false;
              this.cdr.markForCheck();
            },
          });
        },
        error: (err) => {
          this.errorMessage = err?.error?.message ?? 'Failed to save draft before publishing.';
          this.isSubmitting = false;
          this.cdr.markForCheck();
        },
      });
      return;
    }

    this.postingService.createPosting(request).subscribe({
      next: () => {
        this.successMessage = 'Job posting created successfully!';
        this.postingForm.reset({ locationType: 'REMOTE', hrManagerId: this.hrManagerId });
        this.isSubmitting = false;
        this.loadPostings();
      },
      error: (err) => {
        this.errorMessage = err?.error?.message ?? 'Failed to create posting.';
        this.isSubmitting = false;
        this.cdr.markForCheck();
      },
    });
  }
  saveDraft() {
    this.successMessage = '';
    this.errorMessage = '';

    const titleCtrl = this.postingForm.get('title');
    if (!titleCtrl?.value) {
      titleCtrl?.markAsTouched();
      this.errorMessage = 'A title is required, even for a draft.';
      return;
    }

    const raw = this.postingForm.getRawValue();
    const request: PostingRequest = {
      ...raw,
      skillsRequired: raw.skillsRequired
        ? raw.skillsRequired
            .split(',')
            .map((s: string) => s.trim())
            .filter(Boolean)
        : [],
    };

    this.isSubmitting = true;

    const call = this.editingDraftId
      ? this.postingService.updateDraft(this.editingDraftId, request)
      : this.postingService.createDraft(request);

    call.subscribe({
      next: () => {
        this.successMessage = this.editingDraftId ? 'Draft updated.' : 'Draft saved.';
        this.editingDraftId = null;
        this.postingForm.reset({ locationType: 'REMOTE', hrManagerId: this.hrManagerId });
        this.isSubmitting = false;
        this.loadPostings();
      },
      error: (err) => {
        this.errorMessage = err?.error?.message ?? 'Failed to save draft.';
        this.isSubmitting = false;
        this.cdr.markForCheck();
      },
    });
  }
  editDraft(posting: PostingResponse) {
    this.editingDraftId = posting.id;
    this.successMessage = '';
    this.errorMessage = '';

    this.postingForm.patchValue({
      title: posting.title,
      company: posting.company ?? '',
      description: posting.description ?? '',
      skillsRequired: posting.skillsRequired ? posting.skillsRequired.join(', ') : '',
      locationType: posting.locationType ?? 'REMOTE',
      location: posting.location ?? '',
      deadline: posting.deadline ?? '',
      department: posting.department ?? '',
      employmentType: posting.employmentType ?? '',
    });

    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  cancelEdit() {
    this.editingDraftId = null;
    this.postingForm.reset({ locationType: 'REMOTE', hrManagerId: this.hrManagerId });
    this.successMessage = '';
    this.errorMessage = '';
  }

  closePosting(posting: PostingResponse) {
    this.successMessage = '';
    this.errorMessage = '';

    this.postingService.closePosting(posting.id).subscribe({
      next: () => {
        this.successMessage = 'Posting closed.';
        this.loadPostings();
      },
      error: (err) => {
        this.errorMessage = err?.error?.message ?? 'Failed to close posting.';
        this.cdr.markForCheck();
      },
    });
  }
  deleteDraft(posting: PostingResponse) {
    if (!confirm(`Delete draft "${posting.title}"? This cannot be undone.`)) {
      return;
    }

    this.successMessage = '';
    this.errorMessage = '';

    this.postingService.deletePosting(posting.id).subscribe({
      next: () => {
        this.successMessage = 'Draft deleted.';
        if (this.editingDraftId === posting.id) {
          this.cancelEdit();
        }
        this.loadPostings();
      },
      error: (err) => {
        this.errorMessage = err?.error?.message ?? 'Failed to delete draft.';
        this.cdr.markForCheck();
      },
    });
  }
  fieldHasError(field: string): boolean {
    const ctrl = this.postingForm.get(field);
    return !!ctrl && ctrl.invalid && ctrl.touched;
  }

  formatLocationType(type: string | null | undefined): string {
    if (!type) {
      return '—';
    }
    return type === 'ON_SITE' ? 'On-site' : type.charAt(0) + type.slice(1).toLowerCase();
  }
  get filteredPostings(): PostingResponse[] {
    let result = this.postings;

    if (this.statusFilter !== 'ALL') {
      result = result.filter((p) => p.status === this.statusFilter);
    }

    if (this.locationTypeFilter) {
      result = result.filter((p) => p.locationType === this.locationTypeFilter);
    }

    if (this.keywordFilter.trim()) {
      const keyword = this.keywordFilter.trim().toLowerCase();
      result = result.filter(
        (p) =>
          p.title.toLowerCase().includes(keyword) ||
          (p.description ?? '').toLowerCase().includes(keyword),
      );
    }

    return result;
  }

  clearFilters() {
    this.statusFilter = 'ALL';
    this.keywordFilter = '';
    this.locationTypeFilter = '';
  }
}
