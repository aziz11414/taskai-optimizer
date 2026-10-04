import { AiRecommendationResponse } from './ai.models';

export interface AnalyticsResponse {
  totalTasks: number;
  todoTasks: number;
  inProgressTasks: number;
  doneTasks: number;
  highPriorityTasks: number;
  overdueTasks: number;
}

export interface UserDashboardResponse {
  userEmail: string;
  analytics: AnalyticsResponse;
  recommendation: AiRecommendationResponse | null;
  summary: string;
}