import { Component, ChangeDetectorRef } from '@angular/core';
import { RouterLink, Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.errorMessage = '';
      this.authService.login(this.loginForm.value).subscribe({
        next: (res) => {
          // Check for a returnUrl (set by authGuard when a protected page was accessed)
          const returnUrl = this.route.snapshot.queryParams['returnUrl'];
          if (returnUrl) {
            this.router.navigateByUrl(returnUrl);
          } else if (res.role === 'HR_MANAGER') {
            this.router.navigate(['/hr/postings']);
          } else {
            // SUPER_ADMIN → /jobs (with Admin Panel link in nav)
            // CANDIDATE → /jobs
            this.router.navigate(['/jobs']);
          }
        },
        error: (err) => {
          this.errorMessage = err?.error?.message || 'Invalid email or password.';
          this.cdr.markForCheck();
        }
      });
    }
  }
}