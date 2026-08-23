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
  errorMessage: string = '';
  isLoading: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private postingService: PostingService
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.postingService.getPostingById(id).subscribe({
        next: (data: PostingResponse) => {
          this.job = data;
          this.isLoading = false;
        },
        error: (err: any) => {
          console.error('Error fetching job details', err);
          this.errorMessage = 'Failed to load job details.';
          this.isLoading = false;
        }
      });
    } else {
      this.errorMessage = 'No Job ID found in URL.';
      this.isLoading = false;
    }
  }
}