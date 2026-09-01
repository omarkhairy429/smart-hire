import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PostingService } from '../../../core/services/postings.service';
import { PostingResponse } from '../../../core/models/api.models';

@Component({
  selector: 'app-job-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './job-list.html',
  styleUrls: ['./job-list.css'],
})
export class JobListComponent implements OnInit {
  jobs: PostingResponse[] = [];
  keyword = '';
  location = '';
  locationType = '';
  readonly locationTypes = ['REMOTE', 'HYBRID', 'ON_SITE'];

  constructor(private postingService: PostingService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.search();
  }

  search() {
    this.postingService
      .searchPublicPostings({
        keyword: this.keyword,
        location: this.location,
        locationType: this.locationType,
      })
      .subscribe({
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

  clearFilters() {
    this.keyword = '';
    this.location = '';
    this.locationType = '';
    this.search();
  }
}
