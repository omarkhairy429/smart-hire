import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PostingService } from '../../../core/services/postings.service';
import { PostingResponse } from '../../../core/models/api.models';

@Component({
  selector: 'app-job-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './job-list.html',
  styleUrls: ['./job-list.css'],
})
export class JobListComponent implements OnInit {
  jobs: PostingResponse[] = [];

  constructor(private postingService: PostingService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.postingService.getPostings().subscribe({
      next: (data: PostingResponse[]) => {
        this.jobs = data;
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        console.error('Error fetching jobs', err);
        this.cdr.markForCheck();
      },
    });
  }
}
