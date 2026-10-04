import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../../environments/environment';
import { ApiResponse } from '../../../core/models/api.models';
import { AnalyticsResponse, UserDashboardResponse } from '../../../core/models/analytics.models';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiBaseUrl}/analytics`;

  getGlobalTaskStatistics(): Observable<ApiResponse<AnalyticsResponse>> {
    return this.http.get<ApiResponse<AnalyticsResponse>>(`${this.apiUrl}/tasks`);
  }

  getCurrentUserTaskStatistics(): Observable<ApiResponse<AnalyticsResponse>> {
    return this.http.get<ApiResponse<AnalyticsResponse>>(`${this.apiUrl}/my-tasks`);
  }

  getCurrentUserDashboard(): Observable<ApiResponse<UserDashboardResponse>> {
    return this.http.get<ApiResponse<UserDashboardResponse>>(`${this.apiUrl}/dashboard`);
  }
}