import { Component } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { RegisterData } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [RouterLink, ReactiveFormsModule],
  templateUrl: './register.html',
  styleUrls: ['./register.css'],
})
export class RegisterComponent {
  registerForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
  ) {
    this.registerForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  onSubmit() {
    if (this.registerForm.valid) {
      const { firstName, lastName, email, password } = this.registerForm.value;

      // Combine firstName and lastName into a single name field
      const payload: RegisterData = {
        firstName,
        lastName,
        email,
        password,
      };

      this.authService.register(payload).subscribe({
        next: (res) => {
          console.log('Registration successful!', res);
          this.router.navigate(['/jobs']);
        },
        error: (err) => console.error('Registration failed', err),
      });
    }
  }
}
