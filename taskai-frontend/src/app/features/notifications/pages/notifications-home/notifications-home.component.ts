import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { forkJoin } from 'rxjs';

import { NotificationsService } from '../../services/notifications.service';
import { NotificationResponse, NotificationType } from '../../../../core/models/notification.models';
import { NotificationStateService } from '../../../../core/services/notification-state.service';

type NotificationFilter = 'ALL' | 'UNREAD' | NotificationType;

@Component({
  selector: 'app-notifications-home',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './notifications-home.component.html',
  styleUrl: './notifications-home.component.css'
})
export class NotificationsHomeComponent implements OnInit {
  private readonly notificationsService = inject(NotificationsService);
  private readonly toastr = inject(ToastrService);
  private readonly notificationState = inject(NotificationStateService);
  private readonly router = inject(Router);

  loading = true;
  bulkLoading = false;
  notifications: NotificationResponse[] = [];
  unreadCount = 0;

  activeFilter: NotificationFilter = 'ALL';
  readonly itemLoadingIds = new Set<number>();

  ngOnInit(): void {
    this.refreshAll();
  }

  refreshAll(): void {
    this.loading = true;

    forkJoin({
      notifications: this.notificationsService.getMyNotifications(),
      unread: this.notificationsService.getUnreadCount()
    }).subscribe({
      next: ({ notifications, unread }) => {
        this.notifications = notifications;
        this.unreadCount = unread.count;
        this.notificationState.setUnreadCount(unread.count);
        this.loading = false;
      },
      error: () => {
        this.toastr.error('Impossible de charger les notifications');
        this.loading = false;
      }
    });
  }

  get filteredNotifications(): NotificationResponse[] {
    switch (this.activeFilter) {
      case 'UNREAD':
        return this.notifications.filter(n => !n.isRead);
      case 'ALL':
        return this.notifications;
      default:
        return this.notifications.filter(n => n.type === this.activeFilter);
    }
  }

  setFilter(filter: NotificationFilter): void {
    this.activeFilter = filter;
  }

  markAsRead(notification: NotificationResponse): void {
    if (notification.isRead || this.itemLoadingIds.has(notification.id)) return;

    this.itemLoadingIds.add(notification.id);

    this.notificationsService.markAsRead(notification.id).subscribe({
      next: (updated) => {
        this.notifications = this.notifications.map(item =>
          item.id === updated.id ? updated : item
        );

        if (this.unreadCount > 0) {
          this.unreadCount--;
          this.notificationState.setUnreadCount(this.unreadCount);
        }

        this.itemLoadingIds.delete(notification.id);
      },
      error: () => {
        this.toastr.error('Impossible de marquer la notification comme lue');
        this.itemLoadingIds.delete(notification.id);
      }
    });
  }

  markAllAsRead(): void {
    if (this.unreadCount === 0 || this.bulkLoading) return;

    this.bulkLoading = true;

    this.notificationsService.markAllAsRead().subscribe({
      next: () => {
        this.notifications = this.notifications.map(item => ({
          ...item,
          isRead: true
        }));
        this.unreadCount = 0;
        this.notificationState.resetUnreadCount();
        this.toastr.success('Toutes les notifications ont été marquées comme lues');
        this.bulkLoading = false;
      },
      error: () => {
        this.toastr.error('Impossible de marquer toutes les notifications comme lues');
        this.bulkLoading = false;
      }
    });
  }

  deleteNotification(notification: NotificationResponse, event: MouseEvent): void {
    event.stopPropagation();

    if (this.itemLoadingIds.has(notification.id)) return;

    this.itemLoadingIds.add(notification.id);

    this.notificationsService.delete(notification.id).subscribe({
      next: () => {
        const wasUnread = !notification.isRead;
        this.notifications = this.notifications.filter(item => item.id !== notification.id);

        if (wasUnread && this.unreadCount > 0) {
          this.unreadCount--;
          this.notificationState.setUnreadCount(this.unreadCount);
        }

        this.toastr.success('Notification supprimée');
        this.itemLoadingIds.delete(notification.id);
      },
      error: () => {
        this.toastr.error('Impossible de supprimer la notification');
        this.itemLoadingIds.delete(notification.id);
      }
    });
  }

  openNotification(notification: NotificationResponse): void {
    if (!notification.isRead) {
      this.markAsRead(notification);
    }

    if (notification.actionUrl) {
      this.router.navigateByUrl(notification.actionUrl);
      return;
    }

    if (notification.relatedTaskId) {
      this.router.navigate(['/tasks', notification.relatedTaskId]);
    }
  }

  getTypeClass(type: NotificationType): string {
    switch (type) {
      case 'SUCCESS':
        return 'notif-success';
      case 'WARNING':
      case 'DEADLINE_ALERT':
        return 'notif-warning';
      case 'ERROR':
      case 'OVERLOAD_ALERT':
        return 'notif-error';
      case 'AI_RECOMMENDATION':
        return 'notif-ai';
      default:
        return 'notif-info';
    }
  }

  getTypeIcon(type: NotificationType): string {
    switch (type) {
      case 'SUCCESS':
        return 'bi bi-check-circle-fill';
      case 'WARNING':
      case 'DEADLINE_ALERT':
        return 'bi bi-exclamation-triangle-fill';
      case 'ERROR':
      case 'OVERLOAD_ALERT':
        return 'bi bi-x-octagon-fill';
      case 'AI_RECOMMENDATION':
        return 'bi bi-stars';
      default:
        return 'bi bi-info-circle-fill';
    }
  }

  getTypeLabel(type: NotificationType): string {
    switch (type) {
      case 'SUCCESS':
        return 'Succès';
      case 'WARNING':
        return 'Alerte';
      case 'ERROR':
        return 'Erreur';
      case 'INFO':
        return 'Info';
      case 'AI_RECOMMENDATION':
        return 'IA';
      case 'DEADLINE_ALERT':
        return 'Échéance';
      case 'OVERLOAD_ALERT':
        return 'Surcharge';
      default:
        return type;
    }
  }

  trackById(_: number, notification: NotificationResponse): number {
    return notification.id;
  }
}