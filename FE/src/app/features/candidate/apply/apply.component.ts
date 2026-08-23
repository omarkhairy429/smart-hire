import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute, Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PostingService } from '../../../core/services/postings.service';
import { ApplicationService } from '../../../core/services/application.service';
import { PostingResponse } from '../../../core/models/api.models';

@Component({
  selector: 'app-apply',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './apply.component.html',
  styleUrls: ['./apply.component.css']
})
export class ApplyComponent implements OnInit {
  job: PostingResponse | null = null;
  isLoadingJob = true;
  isSubmitting = false;
  successMessage = '';
  errorMessage = '';
  postingId = '';

  applyForm: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private postingService: PostingService,
    private applicationService: ApplicationService,
    private cdr: ChangeDetectorRef
  ) {
    this.applyForm = this.fb.group({
      coverLetter: [''],
      experienceSummary: [''],
      resumeUrl: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.postingId = this.route.snapshot.paramMap.get('id') ?? '';
    if (this.postingId) {
      this.postingService.getPostingById(this.postingId).subscribe({
        next: (job) => {
          this.job = job;
          this.isLoadingJob = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.errorMessage = 'Could not load job details.';
          this.isLoadingJob = false;
          this.cdr.markForCheck();
        }
      });
    }
  }

  onSubmit() {
    if (this.applyForm.invalid) {
      this.applyForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    const { coverLetter, experienceSummary, resumeUrl } = this.applyForm.value;

    this.applicationService.applyToPosting({
      postingId: this.postingId,
      coverLetter,
      experienceSummary,
      resumeUrl
    }).subscribe({
      next: () => {
        this.successMessage = 'Application submitted successfully! Redirecting...';
        this.isSubmitting = false;
        this.cdr.markForCheck();
        setTimeout(() => this.router.navigate(['/my-applications']), 1800);
      },
      error: (err) => {
        this.errorMessage = err?.error?.message ?? 'Failed to submit application. Please try again.';
        this.isSubmitting = false;
        this.cdr.markForCheck();
      }
    });
  }
}
