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
  isActive: boolean;
  createdAt: string;
}

export enum ApplicationStage {
  APPLIED = 'APPLIED',
  SCREENING = 'SCREENING',
  INTERVIEW = 'INTERVIEW',
  OFFERED = 'OFFERED',
  REJECTED = 'REJECTED'
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
  meetingLink: string;
}

export interface InterviewResponse {
  id: string;
  applicationId: string;
  interviewerId: string;
  scheduledAt: string;
  meetingLink: string;
  createdAt: string;
  updatedAt: string;
  interviewerName?: string;
  candidateName?: string;
  postingTitle?: string;
}

export interface DossierResponse {
  interviewId: string;
  scheduledAt: string;
  meetingLink: string;
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
