import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { PostingService } from '../../../core/services/postings.service';
import { AuthService } from '../../../core/services/auth.service';
import { ApplicationService } from '../../../core/services/application.service';
import {
  PostingResponse,
  ApplicationResponse
} from '../../../core/models/api.models';

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

  // Candidate application for this job
  myApplication: ApplicationResponse | null = null;
  isCheckingApplication: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private postingService: PostingService,
    private authService: AuthService,
    private applicationService: ApplicationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.isLoggedIn = this.authService.isLoggedIn();
    this.userRole = this.authService.getRole();

    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.loadJob(id);

      // Only candidates need to check whether they already applied
      if (this.isLoggedIn && this.userRole === 'CANDIDATE') {
        this.loadMyApplication(id);
      }
    } else {
      this.errorMessage = 'No Job ID found in URL.';
      this.isLoading = false;
    }
  }

  private loadJob(id: string) {
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
  }

  private loadMyApplication(postingId: string) {
    this.isCheckingApplication = true;

    this.applicationService.getMyApplications().subscribe({
      next: (applications: ApplicationResponse[]) => {
        this.myApplication =
          applications.find(app => app.postingId === postingId) ?? null;

        this.isCheckingApplication = false;
        this.cdr.markForCheck();

        console.log(
          '[JobDetail] Application for this job:',
          this.myApplication
        );
      },
      error: (err) => {
        console.error(
          '[JobDetail] Failed to load my applications:',
          err
        );

        this.isCheckingApplication = false;
        this.cdr.markForCheck();
      }
    });
  }

  onApplyClick() {
    if (!this.job) return;

    if (!this.isLoggedIn) {
      this.router.navigate(['/login'], {
        queryParams: {
          returnUrl: `/apply/${this.job.id}`
        }
      });
    } else {
      this.router.navigate(['/apply', this.job.id]);
    }
  }

  get isStaff(): boolean {
    return (
      this.userRole === 'HR_MANAGER' ||
      this.userRole === 'SUPER_ADMIN'
    );
  }

  get hasApplied(): boolean {
    return this.myApplication !== null;
  }

  get applicationStage(): string {
    return this.myApplication?.stage ?? '';
  }
}