export type LocationType = 'REMOTE' | 'HYBRID' | 'ON_SITE';
export type PostingStatus = 'PUBLISHED' | 'CLOSED' | 'DRAFT';

export interface PostingRequest {
    hrManagerId: string;
    title: string;
    company: string;
    description: string;
    skillsRequired: string[];
    locationType: LocationType;
    location: string;
    deadline: string;
}

export interface Posting {
    id: string;
    hrManagerId: string;
    title: string;
    company: string;
    description: string;
    skillsRequired: string[];
    locationType: LocationType;
    location: string;
    status: PostingStatus;
    deadline: string | null;
    createdAt: string;
    updatedAt: string;
}