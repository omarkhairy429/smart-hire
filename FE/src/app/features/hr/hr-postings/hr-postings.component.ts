import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PostingService, PostingRequest } from '../../../core/services/postings.service';
import { AuthService } from '../../../core/services/auth.service';
import { PostingResponse } from '../../../core/models/api.models';

const UUID_REGEX = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;

@Component({
  selector: 'app-hr-postings',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './hr-postings.component.html',
  styleUrls: ['./hr-postings.component.css']
})
export class HrPostingsComponent implements OnInit {
  postings: PostingResponse[] = [];
  isLoading = true;
  isSubmitting = false;
  successMessage = '';
  errorMessage = '';
  hrManagerId: string = '';
  hasAutoId = false;

  readonly locationTypes = ['REMOTE', 'HYBRID', 'ON_SITE'];

  postingForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private postingService: PostingService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {
    this.postingForm = this.fb.group({
      hrManagerId: ['', [Validators.required, Validators.pattern(UUID_REGEX)]],
      title: ['', [Validators.required, Validators.maxLength(120)]],
      description: ['', [Validators.required, Validators.maxLength(3000)]],
      skillsRequired: ['', Validators.required],
      locationType: ['REMOTE', Validators.required],
      location: ['', Validators.maxLength(160)],
      deadline: ['', Validators.required]
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
    this.postingService.getPostings().subscribe({
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
      }
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
        .filter(Boolean)
    };

    this.isSubmitting = true;
    this.postingService.createPosting(request).subscribe({
      next: (newPosting) => {
        // Reload from server to guarantee the list is always in sync
        this.successMessage = `Job posting created successfully!`;
        this.postingForm.reset({ locationType: 'REMOTE', hrManagerId: this.hrManagerId });
        this.isSubmitting = false;
        this.loadPostings(); // this also calls cdr.markForCheck()
      },
      error: (err) => {
        this.errorMessage = err?.error?.message ?? 'Failed to create posting.';
        this.isSubmitting = false;
        this.cdr.markForCheck();
      }
    });
  }

  fieldHasError(field: string): boolean {
    const ctrl = this.postingForm.get(field);
    return !!ctrl && ctrl.invalid && ctrl.touched;
  }

  formatLocationType(type: string): string {
    return type === 'ON_SITE' ? 'On-site' : type.charAt(0) + type.slice(1).toLowerCase();
  }
}
