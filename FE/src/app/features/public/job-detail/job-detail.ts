import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { PostingService } from '../../../core/services/postings.service';
import { AuthService } from '../../../core/services/auth.service';
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

  userRole: string | null = null;
  isLoggedIn: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private postingService: PostingService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.isLoggedIn = this.authService.isLoggedIn();
    this.userRole = this.authService.getRole();

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.postingService.getPostingById(id).subscribe({
        next: (data: PostingResponse) => {
          this.job = data;
          this.isLoading = false;
          this.cdr.markForCheck();
        },
        error: (err: any) => {
          console.error('[JobDetail] error:', err);
          this.errorMessage = 'Failed to load job details.';
          this.isLoading = false;
          this.cdr.markForCheck();
        }
      });
    } else {
      this.errorMessage = 'No Job ID found in URL.';
      this.isLoading = false;
    }
  }

  onApplyClick() {
    if (!this.job) return;
    if (!this.isLoggedIn) {
      // Redirect to login, preserving the intended destination
      this.router.navigate(['/login'], { queryParams: { returnUrl: `/apply/${this.job.id}` } });
    } else {
      this.router.navigate(['/apply', this.job.id]);
    }
  }

  /** True when the logged-in user is HR or Admin (should not apply) */
  get isStaff(): boolean {
    return this.userRole === 'HR_MANAGER' || this.userRole === 'SUPER_ADMIN';
  }
}