export interface PostingResponse {
  id: string;
  hrManagerId: string;
  title: string;
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

export interface ApplicationResponse {
  id: string;
  postingId: string;
  candidateId: string;
  coverLetter: string;
  experienceSummary: string;
  resumeUrl: string;
  stage: string;
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