package com.taskai.optimizer.services;

import com.taskai.optimizer.dto.response.AnalyticsResponse;
import com.taskai.optimizer.dto.response.UserDashboardResponse;

public interface AnalyticsService {

    AnalyticsResponse getTaskStatistics();

    AnalyticsResponse getCurrentUserTaskStatistics();

    UserDashboardResponse getCurrentUserDashboard();
}