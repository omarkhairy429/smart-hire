import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AdminService } from '../../../core/services/admin';
import { StaffResponse } from '../../../core/models/api.models';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-dashboard.html',
  styleUrls: ['./admin-dashboard.css'],
})
export class AdminDashboardComponent implements OnInit {
  staffList: StaffResponse[] = [];
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
      companyName: ['', Validators.required],
    });
  }

  ngOnInit() {
    this.loadStaff();
  }

  loadStaff() {
    this.adminService.getStaff().subscribe({
      next: (data: StaffResponse[]) => {
        this.staffList = data;
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        console.error('Error fetching staff', err);
        this.cdr.markForCheck();
      },
    });
  }

  onSubmit() {
    if (this.staffForm.invalid) {
      this.staffForm.markAllAsTouched();
      return;
    }

    const formValue = this.staffForm.value;

    this.adminService.createStaff({
      firstName: formValue.firstName,
      lastName: formValue.lastName,
      email: formValue.email,
      password: formValue.password,
      role: formValue.role,
      companyName: formValue.companyName,
    }).subscribe({
      next: () => {
        this.successMessage = 'Staff member created successfully.';
        this.errorMessage = '';
        this.staffForm.reset({ role: 'HR_MANAGER' });
        this.loadStaff();
        this.cdr.markForCheck();
      },
      error: (err: any) => {
        this.errorMessage = err?.error?.message ?? 'Failed to create staff member.';
        this.successMessage = '';
        console.error('Failed to create staff', err);
        this.cdr.markForCheck();
      },
    });
  }
}