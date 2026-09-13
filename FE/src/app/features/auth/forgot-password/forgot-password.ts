import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './forgot-password.html',
  styleUrls: ['./forgot-password.css']
})
export class ForgotPasswordComponent {
  forgotForm: FormGroup;
  errorMessage = '';
  successMessage = '';
  loading = false;

  constructor(private fb: FormBuilder, private authService: AuthService) {
    this.forgotForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  onSubmit() {
    if (this.forgotForm.invalid) return;

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const { email } = this.forgotForm.value;

    this.authService.forgotPassword(email).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = 'Check your inbox — we sent a reset link to ' + email + '.';
        this.forgotForm.reset();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = this.resolveError(err);
      }
    });
  }

  private resolveError(err: any): string {
    const status = err?.status;
    const msg = err?.error?.message;

    if (status === 404 || (msg && msg.toLowerCase().includes('not found'))) {
      return 'No account is associated with that email address.';
    }
    if (status === 503) {
      return msg || 'We could not send the email right now. Please try again in a few minutes.';
    }
    return msg || 'Something went wrong. Please try again.';
  }
}
