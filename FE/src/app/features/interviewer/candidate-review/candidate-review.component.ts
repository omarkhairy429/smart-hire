import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { InterviewService } from '../../../core/services/interview.service';
import {
  DossierResponse,
  FeedbackRecommendation,
  FeedbackResponse
} from '../../../core/models/api.models';

@Component({
  selector: 'app-candidate-review',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './candidate-review.component.html',
  styleUrls: ['./candidate-review.component.css']
})
export class CandidateReviewComponent implements OnInit {
  dossier?: DossierResponse;
  isLoading = true;
  errorMessage = '';

  interviewId = '';

  rating: number | null = null;
  technicalScore: number | null = null;
  communicationScore: number | null = null;
  recommendation: FeedbackRecommendation | '' = '';
  comments = '';

  scores = [1, 2, 3, 4, 5];
  recommendations = [
    { value: FeedbackRecommendation.PROCEED, label: 'Proceed' },
    { value: FeedbackRecommendation.HOLD, label: 'Hold' },
    { value: FeedbackRecommendation.REJECT, label: 'Reject' }
  ];

  existingFeedback?: FeedbackResponse;
  isSavingFeedback = false;
  feedbackError = '';
  feedbackSaved = false;

  constructor(
    private route: ActivatedRoute,
    private interviewService: InterviewService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.errorMessage = 'Missing interview id.';
      this.isLoading = false;
      return;
    }

    this.interviewId = id;
    this.loadFeedback(id);

    this.interviewService.getDossier(id).subscribe({
      next: (data) => {
        this.dossier = data;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.errorMessage = err?.status === 403
          ? 'This interview is not assigned to you.'
          : 'Could not load the candidate details.';
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  /** A 404 here just means no feedback has been left yet. */
  private loadFeedback(interviewId: string) {
    this.interviewService.getMyFeedback(interviewId).subscribe({
      next: (feedback) => {
        this.existingFeedback = feedback;
        this.rating = feedback.rating;
        this.technicalScore = feedback.technicalScore ?? null;
        this.communicationScore = feedback.communicationScore ?? null;
        this.recommendation = feedback.recommendation;
        this.comments = feedback.comments ?? '';
        this.cdr.markForCheck();
      },
      error: () => {}
    });
  }

  submitFeedback() {
    if (!this.rating || !this.recommendation) {
      this.feedbackError = 'Please give an overall rating and a recommendation.';
      return;
    }

    this.isSavingFeedback = true;
    this.feedbackError = '';
    this.feedbackSaved = false;

    this.interviewService.submitFeedback(this.interviewId, {
      rating: this.rating,
      technicalScore: this.technicalScore,
      communicationScore: this.communicationScore,
      recommendation: this.recommendation as FeedbackRecommendation,
      comments: this.comments.trim()
    }).subscribe({
      next: (feedback) => {
        this.existingFeedback = feedback;
        this.isSavingFeedback = false;
        this.feedbackSaved = true;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.feedbackError = err?.status === 403
          ? 'This interview is not assigned to you.'
          : 'Could not save your feedback.';
        this.isSavingFeedback = false;
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
