import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';

import { AiInsightsService } from '../../services/ai-insights.service';
import { TasksService } from '../../../tasks/services/tasks.service';
import { TaskResponse } from '../../../../core/models/task.models';
import {
  AiRecommendationListResponse,
  AiRecommendationResponse,
  TaskAnalysisResponse
} from '../../../../core/models/ai.models';
import {
  formatScore,
  getConfidenceClass,
  getLevelClass,
  getPriorityClass,
  getScoreWidth,
  getStatusClass
} from '../../../../core/utils/task-ui.utils';

@Component({
  selector: 'app-ai-insights-home',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  templateUrl: './ai-insights-home.component.html',
  styleUrl: './ai-insights-home.component.css'
})
export class AiInsightsHomeComponent implements OnInit {
  private readonly aiService = inject(AiInsightsService);
  private readonly tasksService = inject(TasksService);
  private readonly toastr = inject(ToastrService);

  loading = true;
  analyzing = false;

  bestTask: AiRecommendationResponse | null = null;
  topTasks: AiRecommendationListResponse = {
    userEmail: '',
    totalCandidates: 0,
    recommendations: []
  };

  tasks: TaskResponse[] = [];
  selectedTaskId: number | null = null;
  analysis: TaskAnalysisResponse | null = null;

  ngOnInit(): void {
    this.loadPageData();
  }

  loadPageData(): void {
    this.loading = true;

    forkJoin({
      bestTask: this.aiService.recommendNextBestTask().pipe(catchError(() => of(null))),
      topTasks: this.aiService.recommendTopTasks(5).pipe(
        catchError(() =>
          of({
            userEmail: '',
            totalCandidates: 0,
            recommendations: []
          } as AiRecommendationListResponse)
        )
      ),
      tasks: this.tasksService.getAll().pipe(
        catchError(() => of([] as TaskResponse[]))
      )
    })
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: ({ bestTask, topTasks, tasks }) => {
          this.bestTask = bestTask;
          this.topTasks = topTasks;
          this.tasks = tasks.filter(task => task.status !== 'DONE');

          if (!topTasks.recommendations.length && !bestTask) {
            this.toastr.info('Aucune recommandation IA disponible pour le moment');
          }
        },
        error: () => {
          this.toastr.error('Impossible de charger les insights IA');
        }
      });
  }

  analyzeSelectedTask(): void {
    if (!this.selectedTaskId) return;

    this.analyzing = true;
    this.analysis = null;

    this.aiService.analyzeTask(this.selectedTaskId)
      .pipe(finalize(() => (this.analyzing = false)))
      .subscribe({
        next: (data) => {
          this.analysis = data;
        },
        error: () => {
          this.analysis = null;
          this.toastr.error('Impossible d’analyser cette tâche');
        }
      });
  }

  getStatusClass = getStatusClass;
  getPriorityClass = getPriorityClass;
  getLevelClass = getLevelClass;
  getConfidenceClass = getConfidenceClass;
  getScoreWidth = getScoreWidth;
  formatScore = formatScore;

  isLikelyLate(probability?: number): boolean {
    return (probability ?? 0) >= 60;
  }

  getLevelLabel(level?: string): string {
    switch (level) {
      case 'HIGH':
        return 'Élevé';
      case 'MEDIUM':
        return 'Moyen';
      case 'LOW':
        return 'Faible';
      default:
        return level || '-';
    }
  }

  getStatusLabel(status?: string): string {
    switch (status) {
      case 'TODO':
        return 'À faire';
      case 'IN_PROGRESS':
        return 'En cours';
      case 'DONE':
        return 'Terminée';
      default:
        return status || '-';
    }
  }

  getPriorityLabel(priority?: string): string {
    switch (priority) {
      case 'HIGH':
        return 'Haute';
      case 'MEDIUM':
        return 'Moyenne';
      case 'LOW':
        return 'Faible';
      default:
        return priority || '-';
    }
  }
}