import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgChartsModule } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';

import { AnalyticsService } from '../../services/analytics.service';
import { AnalyticsResponse } from '../../../../core/models/analytics.models';

@Component({
  selector: 'app-analytics-home',
  standalone: true,
  imports: [CommonModule, NgChartsModule],
  templateUrl: './analytics-home.component.html',
  styleUrl: './analytics-home.component.css'
})
export class AnalyticsHomeComponent implements OnInit {
  private readonly analyticsService = inject(AnalyticsService);

  loading = true;
  analytics: AnalyticsResponse | null = null;

  readonly pieChartType = 'doughnut' as const;
  readonly barChartType = 'bar' as const;

  pieChartData: ChartConfiguration<'doughnut'>['data'] = {
    labels: ['TODO', 'IN_PROGRESS', 'DONE'],
    datasets: [
      {
        data: [],
        backgroundColor: ['#3b82f6', '#f59e0b', '#10b981'],
        borderWidth: 0
      }
    ]
  };

  pieChartOptions: ChartConfiguration<'doughnut'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'bottom'
      }
    },
    cutout: '68%'
  };

  barChartData: ChartConfiguration<'bar'>['data'] = {
    labels: ['High Priority', 'Overdue'],
    datasets: [
      {
        label: 'Tasks',
        data: [],
        backgroundColor: ['#ef4444', '#8b5cf6'],
        borderRadius: 10,
        maxBarThickness: 52
      }
    ]
  };

  barChartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false
      }
    },
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          precision: 0
        }
      }
    }
  };

  ngOnInit(): void {
    this.analyticsService.getCurrentUserTaskStatistics().subscribe({
      next: (res) => {
        this.analytics = res.data;

        this.pieChartData.datasets[0].data = [
          this.analytics.todoTasks,
          this.analytics.inProgressTasks,
          this.analytics.doneTasks
        ];

        this.barChartData.datasets[0].data = [
          this.analytics.highPriorityTasks,
          this.analytics.overdueTasks
        ];

        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
}