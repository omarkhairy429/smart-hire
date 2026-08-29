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
