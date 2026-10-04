export type NotificationType =
  | 'INFO'
  | 'SUCCESS'
  | 'WARNING'
  | 'ERROR'
  | 'AI_RECOMMENDATION'
  | 'DEADLINE_ALERT'
  | 'OVERLOAD_ALERT';

export interface NotificationRequest {
  title: string;
  message: string;
  type: NotificationType;
  userId: number;
}

export interface NotificationResponse {
  id: number;
  title: string;
  message: string;
  type: NotificationType;
  isRead: boolean;
  createdAt: string;
  userId: number;
  userFullName: string;
  userEmail: string;
  relatedTaskId?: number | null;
  actionUrl?: string | null;
}

export interface UnreadCountResponse {
  count: number;
}

export interface MessageResponse {
  message: string;
}