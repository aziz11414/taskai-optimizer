import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { interval, startWith, Subscription, switchMap } from 'rxjs';

import { TokenService } from '../../core/services/token.service';
import { NotificationsService } from '../../features/notifications/services/notifications.service';
import { NotificationStateService } from '../../core/services/notification-state.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent implements OnInit, OnDestroy {
  private readonly tokenService = inject(TokenService);
  private readonly notificationsService = inject(NotificationsService);
  private readonly notificationState = inject(NotificationStateService);

  unreadCount = this.notificationState.unreadCount;
  private sub?: Subscription;

  ngOnInit(): void {
    this.startPolling();
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  private startPolling(): void {
    this.sub = interval(30000)
      .pipe(
        startWith(0),
        switchMap(() => this.notificationsService.getUnreadCount())
      )
      .subscribe({
        next: (res) => this.notificationState.setUnreadCount(res.count),
        error: () => this.notificationState.setUnreadCount(0)
      });
  }

  isAdmin(): boolean {
    return this.tokenService.isAdmin();
  }

  isManager(): boolean {
    return this.tokenService.getRole() === 'MANAGER';
  }
}