import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class NotificationStateService {
  readonly unreadCount = signal(0);

  setUnreadCount(count: number): void {
    this.unreadCount.set(Math.max(0, count));
  }

  incrementUnreadCount(): void {
    this.unreadCount.update(count => count + 1);
  }

  decrementUnreadCount(): void {
    this.unreadCount.update(count => Math.max(0, count - 1));
  }

  resetUnreadCount(): void {
    this.unreadCount.set(0);
  }
}