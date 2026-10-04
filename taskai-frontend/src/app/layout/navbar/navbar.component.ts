import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { interval, of, Subscription } from 'rxjs';
import { catchError, startWith, switchMap } from 'rxjs/operators';

import { AuthService } from '../../core/services/auth.service';
import { NotificationsService } from '../../features/notifications/services/notifications.service';
import { NotificationStateService } from '../../core/services/notification-state.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit, OnDestroy {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly notificationsService = inject(NotificationsService);
  private readonly notificationState = inject(NotificationStateService);

  readonly unreadCount = this.notificationState.unreadCount;
  private pollingSubscription?: Subscription;

  ngOnInit(): void {
    this.startUnreadPolling();
  }

  ngOnDestroy(): void {
    this.pollingSubscription?.unsubscribe();
  }

  private startUnreadPolling(): void {
    this.pollingSubscription = interval(30000)
      .pipe(
        startWith(0),
        switchMap(() =>
          this.notificationsService.getUnreadCount().pipe(
            catchError(() => of({ count: 0 }))
          )
        )
      )
      .subscribe((data) => {
        this.notificationState.setUnreadCount(data.count);
      });
  }

  logout(): void {
    this.pollingSubscription?.unsubscribe();
    this.authService.logout();
    this.notificationState.resetUnreadCount();
    this.router.navigate(['/login']);
  }
}