export interface AiRecommendationResponse {
  taskId: number;
  title: string;
  description: string;
  status: string;
  priority: string;
  priorityScore: number;
  riskScore: number;
  combinedScore: number;
  delayProbability: number;
  completionProbability: number;
  confidenceScore: number;
  predictedCompletionAt: string | null;
  priorityLevel: string;
  riskLevel: string;
  assignedUser: string | null;
  summary: string;
  reasons: string[];
}

export interface AiRecommendationListResponse {
  userEmail: string;
  totalCandidates: number;
  recommendations: AiRecommendationResponse[];
}

export interface TaskAnalysisResponse {
  taskId: number;
  title: string;
  description: string;
  status: string;
  priority: string;
  dueDate: string | null;
  assignedUser: string | null;
  priorityScore: number;
  riskScore: number;
  combinedScore: number;
  delayProbability: number;
  completionProbability: number;
  confidenceScore: number;
  predictedCompletionAt: string | null;
  priorityLevel: string;
  riskLevel: string;
  summary: string;
  reasons: string[];
}