import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { PostingService } from '../../../core/services/postings.service';
import { PostingResponse } from '../../../core/models/api.models';

@Component({
  selector: 'app-job-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './job-detail.html',
  styleUrls: ['./job-detail.css']
})
export class JobDetailComponent implements OnInit {
  job: PostingResponse | null = null;
  errorMessage: string = ''; // Added to track errors
  isLoading: boolean = true; // Added to track loading state

  constructor(
    private route: ActivatedRoute,
    private postingService: PostingService
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.postingService.getPostingById(id).subscribe({
        next: (data) => {
          this.job = data;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error fetching job details', err);
          this.errorMessage = 'Failed to load job details. The backend might have thrown an error.';
          this.isLoading = false;
        }
      });
    } else {
      this.errorMessage = 'No Job ID found in the URL.';
      this.isLoading = false;
    }
  }
}