export interface PostingResponse {
  id: string;
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