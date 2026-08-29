import { Component, EventEmitter, Input, OnInit, Output, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InterviewService } from '../../../core/services/interview.service';
import { StaffResponse } from '../../../core/models/api.models';
import { InterviewResponse } from '../../../core/models/api.models';

@Component({
  selector: 'app-hr-schedule-interview',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './hr-schedule-interview.component.html',
  styleUrls: ['./hr-schedule-interview.component.css']
})
export class HrScheduleInterviewComponent implements OnInit {
  @Input({ required: true }) applicationId!: string;
  @Input() candidateName = '';

  @Output() closed = new EventEmitter<void>();
  @Output() scheduled = new EventEmitter<InterviewResponse>();

  interviewers: StaffResponse[] = [];
  interviewerId = '';
  scheduledAt = '';
  meetingLink = '';

  isLoading = true;
  isSaving = false;
  errorMessage = '';

  constructor(
    private interviewService: InterviewService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.interviewService.getInterviewers().subscribe({
      next: (data) => {
        this.interviewers = data;
        this.isLoading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.errorMessage = 'Could not load interviewers.';
        this.isLoading = false;
        this.cdr.markForCheck();
      }
    });
  }

  submit() {
    if (!this.interviewerId || !this.scheduledAt || !this.meetingLink.trim()) {
      this.errorMessage = 'Please fill in the interviewer, date and meeting link.';
      return;
    }

    this.isSaving = true;
    this.errorMessage = '';

    this.interviewService.scheduleInterview(this.applicationId, {
      interviewerId: this.interviewerId,
      scheduledAt: this.scheduledAt,
      meetingLink: this.meetingLink.trim()
    }).subscribe({
      next: (interview) => {
        this.isSaving = false;
        this.scheduled.emit(interview);
        this.closed.emit();
      },
      error: (err) => {
        this.errorMessage = err?.error?.message ?? 'Could not schedule the interview.';
        this.isSaving = false;
        this.cdr.markForCheck();
      }
    });
  }

  close() {
    this.closed.emit();
  }

  fullName(staff: StaffResponse): string {
    return `${staff.firstName} ${staff.lastName}`;
  }
}
