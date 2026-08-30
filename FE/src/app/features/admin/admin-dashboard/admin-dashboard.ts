import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AdminService } from '../../../core/services/admin';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-dashboard.html',
  styleUrls: ['./admin-dashboard.css'],
})
export class AdminDashboardComponent implements OnInit {
  staffList: any[] = [];
  successMessage = '';
  errorMessage = '';
  staffForm: FormGroup;

  constructor(
    private adminService: AdminService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef
  ) {
    this.staffForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      role: ['HR_MANAGER', Validators.required],
    });
  }

  ngOnInit() {
    this.loadStaff();
  }

  loadStaff() {
    this.adminService.getStaff().subscribe({
      next: (data: any) => {
        this.staffList = data;
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        console.log('Error fetching staff', err);
        this.cdr.markForCheck();
      },
    });
  }

  onSubmit() {
    if (this.staffForm.invalid) {
      this.staffForm.markAllAsTouched();
      return;
    }

    this.adminService.createStaff(this.staffForm.value).subscribe({
      next: () => {
        this.successMessage = 'Staff member created successfully.';
        this.errorMessage = '';
        this.staffForm.reset({ role: 'HR_MANAGER' });
        this.loadStaff();
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        this.errorMessage = 'Failed to create staff member.';
        console.error('Failed to create staff', err);
        this.cdr.markForCheck();
      },
    });
  }
}