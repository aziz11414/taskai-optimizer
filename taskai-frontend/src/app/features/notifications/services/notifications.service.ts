import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  MessageResponse,
  NotificationRequest,
  NotificationResponse,
  UnreadCountResponse
} from '../../../core/models/notification.models';

@Injectable({
  providedIn: 'root'
})
export class NotificationsService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiBaseUrl}/notifications`;

  getMyNotifications(): Observable<NotificationResponse[]> {
    return this.http.get<NotificationResponse[]>(this.apiUrl);
  }

  getUnreadCount(): Observable<UnreadCountResponse> {
    return this.http.get<UnreadCountResponse>(`${this.apiUrl}/unread-count`);
  }

  markAsRead(id: number): Observable<NotificationResponse> {
    return this.http.put<NotificationResponse>(`${this.apiUrl}/${id}/read`, {});
  }

  markAllAsRead(): Observable<MessageResponse> {
    return this.http.put<MessageResponse>(`${this.apiUrl}/read-all`, {});
  }

  create(payload: NotificationRequest): Observable<NotificationResponse> {
    return this.http.post<NotificationResponse>(this.apiUrl, payload);
  }

  delete(id: number): Observable<MessageResponse> {
    return this.http.delete<MessageResponse>(`${this.apiUrl}/${id}`);
  }
}