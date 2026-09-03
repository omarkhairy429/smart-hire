import { NotificationType } from "./notification-type";

export interface Notification {
    id: string;
    type: NotificationType;
    title: string;
    message: string;
    relatedEntityId: string | null;
    read: boolean;
    createdAt: string;
}