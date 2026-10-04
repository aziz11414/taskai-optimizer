package com.taskai.optimizer.services;

import com.taskai.optimizer.dto.response.AiRecommendationListResponse;
import com.taskai.optimizer.dto.response.AiRecommendationResponse;
import com.taskai.optimizer.dto.response.AiTaskAnalysisResponse;

public interface AiService {

    AiTaskAnalysisResponse analyzeTask(Long taskId);

    AiRecommendationResponse recommendNextBestTaskForCurrentUser();

    AiRecommendationListResponse recommendTopTasksForCurrentUser(int limit);
}