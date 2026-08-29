import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { InterviewService } from '../../../core/services/interview.service';
import { DossierResponse } from '../../../core/models/interview.models';

@Component({
  selector: 'app-candidate-review',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './candidate-review.component.html',
  styleUrls: ['./candidate-review.component.css']
})
export class CandidateReviewComponent implements OnInit {
  dossier?: DossierResponse;
  isLoading = true;
  errorMessage = '';

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

  formatDateTime(value: string): string {
    if (!value) return '—';
    return new Date(value).toLocaleString('en-US', {
      year: 'numeric', month: 'short', day: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  }
}
