export interface PostingResponse {
  id: string;
  hrManagerId: string;
  title: string;
  company: string;
  description: string;
  skillsRequired: string[];
  locationType: string;
  location: string;
  status: string;
  deadline: string;
  department?: string;
  employmentType?: string;
  createdAt: string;
  updatedAt: string;
}

export interface AuthResponse {
  token: string;
  role: string;
  email: string;
  firstName: string;
}

export interface StaffResponse {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
  active: boolean;
  companyName?: string;
  createdAt: string;
}

export enum ApplicationStage {
  APPLIED = 'APPLIED',
  SCREENING = 'SCREENING',
  INTERVIEW = 'INTERVIEW',
  OFFERED = 'OFFERED',
  HIRED = 'HIRED',
  REJECTED = 'REJECTED',
}

export enum InterviewFormat {
  IN_PERSON = 'IN_PERSON',
  VIDEO = 'VIDEO',
  PHONE = 'PHONE',
}

export enum EmploymentType {
  FULL_TIME = 'FULL_TIME',
  PART_TIME = 'PART_TIME',
  CONTRACT = 'CONTRACT',
  INTERNSHIP = 'INTERNSHIP',
  FREELANCE = 'FREELANCE',
}

export interface ApplicationResponse {
  id: string;
  postingId: string;
  candidateId: string;
  candidateName?: string;
  candidateEmail?: string;
  coverLetter?: string;
  experienceSummary?: string;
  resumeUrl?: string;
  stage: ApplicationStage;
  status: string;
  createdAt: string;
  updatedAt: string;
}

export interface ApplyRequest {
  postingId: string;
  coverLetter?: string;
  experienceSummary?: string;
  resumeUrl: string;
}

export interface CandidateNoteRequest {
  candidateId: string;
  content: string;
}

export interface CandidateNoteResponse {
  id: string;
  content: string;
  authorName: string;
  createdAt: string;
}

export interface PipelineResponse {
  stage: ApplicationStage;
  applications: ApplicationResponse[];
}

export interface ScheduleInterviewRequest {
  interviewerId: string;
  scheduledAt: string;
  format: string;
  location?: string;
  meetingLink?: string;
}

export interface InterviewResponse {
  id: string;
  applicationId: string;
  interviewerId: string;
  scheduledAt: string;
  format: string;
  location?: string;
  meetingLink?: string;
  createdAt: string;
  updatedAt: string;
  interviewerName?: string;
  candidateName?: string;
  postingTitle?: string;
}

export interface DossierResponse {
  interviewId: string;
  scheduledAt: string;
  format: string;
  location?: string;
  meetingLink?: string;
  candidateId: string;
  candidateName?: string;
  candidateEmail?: string;
  resumeUrl?: string;
  coverLetter?: string;
  experienceSummary?: string;
  postingId: string;
  postingTitle?: string;
  postingDescription?: string;
  skillsRequired?: string[];
}
export interface AuditLogResponse {
  id: string;
  actorId: string | null;
  actorName: string;
  action: string;
  entityType: string;
  entityId: string;
  details: Record<string, unknown> | null;
  createdAt: string;
}

export interface PageResponse<T> {
  content: T[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface PlatformStatsResponse {
  totalUsers: number;
  activeStaff: number;
  inactiveStaff: number;
  totalPostings: number;
  publishedPostings: number;
  totalApplications: number;
}
