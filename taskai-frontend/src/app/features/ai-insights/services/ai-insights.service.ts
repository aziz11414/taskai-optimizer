import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../../environments/environment';
import {
  AiRecommendationListResponse,
  AiRecommendationResponse,
  TaskAnalysisResponse
} from '../../../core/models/ai.models';

@Injectable({
  providedIn: 'root'
})
export class AiInsightsService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiBaseUrl}/ai`;

  analyzeTask(taskId: number): Observable<TaskAnalysisResponse> {
    return this.http.get<TaskAnalysisResponse>(`${this.apiUrl}/tasks/${taskId}/analyze`);
  }

  recommendNextBestTask(): Observable<AiRecommendationResponse | null> {
    return this.http.get<AiRecommendationResponse | null>(`${this.apiUrl}/tasks/recommendation`);
  }

  recommendTopTasks(limit = 5): Observable<AiRecommendationListResponse> {
    const params = new HttpParams().set('limit', limit);
    return this.http.get<AiRecommendationListResponse>(`${this.apiUrl}/tasks/recommendations`, { params });
  }
}