import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { forkJoin, of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';

import { AnalyticsService } from '../../../analytics/services/analytics.service';
import { UserDashboardResponse, AnalyticsResponse } from '../../../../core/models/analytics.models';

@Component({
  selector: 'app-dashboard-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard-home.component.html',
  styleUrl: './dashboard-home.component.css'
})
export class DashboardHomeComponent implements OnInit {
  private readonly analyticsService = inject(AnalyticsService);
  private readonly toastr = inject(ToastrService);

  loading = true;
  dashboard: UserDashboardResponse | null = null;

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.loading = true;

    forkJoin({
      dashboardRes: this.analyticsService.getCurrentUserDashboard().pipe(
        catchError(() => of(null))
      ),
      analyticsRes: this.analyticsService.getCurrentUserTaskStatistics().pipe(
        catchError(() => of(null))
      )
    })
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: ({ dashboardRes, analyticsRes }) => {
          const dashboardData = dashboardRes?.data ?? null;
          const analyticsData = analyticsRes?.data ?? null;

          if (dashboardData?.analytics) {
            this.dashboard = dashboardData;
            return;
          }

          if (analyticsData) {
            this.dashboard = this.buildFallbackDashboard(analyticsData);
            return;
          }

          this.dashboard = null;
          this.toastr.error('Impossible de charger les données du dashboard');
        },
        error: () => {
          this.dashboard = null;
          this.toastr.error('Impossible de charger le dashboard');
        }
      });
  }

  private buildFallbackDashboard(analytics: AnalyticsResponse): UserDashboardResponse {
    return {
      userEmail: '',
      analytics,
      recommendation: null,
      summary: this.buildFallbackSummary(analytics)
    };
  }

  private buildFallbackSummary(analytics: AnalyticsResponse): string {
    return `Vous avez ${analytics.totalTasks} tâche(s), dont ${analytics.todoTasks} à faire, ${analytics.inProgressTasks} en cours et ${analytics.overdueTasks} en retard.`;
  }

  hasDashboardData(): boolean {
    return !!this.dashboard?.analytics;
  }

  getCompletionRate(): number {
    const total = this.dashboard?.analytics?.totalTasks ?? 0;
    const done = this.dashboard?.analytics?.doneTasks ?? 0;

    if (total === 0) return 0;
    return Math.round((done / total) * 100);
  }

  getCompletionWidth(): string {
    return `${this.getCompletionRate()}%`;
  }

  getWorkloadLabel(): string {
    const analytics = this.dashboard?.analytics;
    if (!analytics) return 'Unknown';

    const { totalTasks, highPriorityTasks, overdueTasks } = analytics;

    if (overdueTasks >= 2 || highPriorityTasks >= 3 || totalTasks >= 8) {
      return 'Élevée';
    }

    if (overdueTasks >= 1 || highPriorityTasks >= 2 || totalTasks >= 5) {
      return 'Modérée';
    }

    return 'Maîtrisée';
  }

  getWorkloadClass(): string {
    switch (this.getWorkloadLabel()) {
      case 'Élevée':
        return 'badge badge-high';
      case 'Modérée':
        return 'badge badge-medium';
      default:
        return 'badge badge-low';
    }
  }

  formatScore(score?: number): string {
    return `${Math.round(score ?? 0)}`;
  }

  getScoreWidth(score?: number): string {
    return `${Math.max(0, Math.min(score ?? 0, 100))}%`;
  }

  getStatusClass(status?: string): string {
    switch (status) {
      case 'TODO':
        return 'badge badge-todo';
      case 'IN_PROGRESS':
        return 'badge badge-progress';
      case 'DONE':
        return 'badge badge-done';
      default:
        return 'badge bg-secondary';
    }
  }

  getPriorityClass(priority?: string): string {
    switch (priority) {
      case 'HIGH':
        return 'badge badge-high';
      case 'MEDIUM':
        return 'badge badge-medium';
      case 'LOW':
        return 'badge badge-low';
      default:
        return 'badge bg-secondary';
    }
  }

  getConfidenceClass(score?: number): string {
    const value = score ?? 0;
    if (value >= 80) return 'badge badge-done';
    if (value >= 60) return 'badge badge-progress';
    return 'badge badge-medium';
  }

  isLikelyLate(probability?: number): boolean {
    return (probability ?? 0) >= 60;
  }
}