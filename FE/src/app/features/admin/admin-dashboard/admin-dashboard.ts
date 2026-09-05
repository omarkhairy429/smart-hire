import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AdminService } from '../../../core/services/admin';
import {
  StaffResponse,
  AuditLogResponse,
  PageResponse,
  PlatformStatsResponse,
} from '../../../core/models/api.models';

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

  stats: PlatformStatsResponse | null = null;
  auditLogs: AuditLogResponse[] = [];
  auditPage = 0;
  auditTotalPages = 0;

  constructor(
    private adminService: AdminService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef,
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
    this.loadStats();
    this.loadAuditLogs();
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

    this.adminService
      .createStaff({
        firstName: formValue.firstName,
        lastName: formValue.lastName,
        email: formValue.email,
        password: formValue.password,
        role: formValue.role,
        companyName: formValue.companyName,
      })
      .subscribe({
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
  loadStats() {
    this.adminService.getStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.cdr.markForCheck();
      },
      error: () => this.cdr.markForCheck(),
    });
  }

  loadAuditLogs(page: number = 0) {
    this.adminService.getAuditLogs(page).subscribe({
      next: (data: PageResponse<AuditLogResponse>) => {
        this.auditLogs = data.content;
        this.auditPage = data.number;
        this.auditTotalPages = data.totalPages;
        this.cdr.markForCheck();
      },
      error: () => this.cdr.markForCheck(),
    });
  }

  toggleStaffStatus(staff: StaffResponse) {
    this.successMessage = '';
    this.errorMessage = '';

    const call = staff.active
      ? this.adminService.deactivateStaff(staff.id)
      : this.adminService.reactivateStaff(staff.id);

    call.subscribe({
      next: () => {
        this.successMessage = staff.active
          ? `${staff.firstName} ${staff.lastName} has been deactivated.`
          : `${staff.firstName} ${staff.lastName} has been reactivated.`;
        this.loadStaff();
        this.loadStats();
        this.loadAuditLogs(this.auditPage);
      },
      error: (err: any) => {
        this.errorMessage = err?.error?.message ?? 'Failed to update account status.';
        this.cdr.markForCheck();
      },
    });
  }

  formatDetails(details: Record<string, unknown> | null): string {
    if (!details) {
      return '—';
    }
    return Object.entries(details)
      .map(([key, value]) => `${key}: ${value}`)
      .join(', ');
  }
}
