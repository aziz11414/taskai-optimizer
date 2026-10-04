package com.taskai.optimizer.services.Impl;

import com.taskai.optimizer.dto.response.AiRecommendationResponse;
import com.taskai.optimizer.dto.response.AnalyticsResponse;
import com.taskai.optimizer.dto.response.UserDashboardResponse;
import com.taskai.optimizer.enums.TaskPriority;
import com.taskai.optimizer.enums.TaskStatus;
import com.taskai.optimizer.exception.BusinessException;
import com.taskai.optimizer.repository.TaskRepository;
import com.taskai.optimizer.services.AiService;
import com.taskai.optimizer.services.AnalyticsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final TaskRepository taskRepository;
    private final AiService aiService;

    public AnalyticsServiceImpl(TaskRepository taskRepository, AiService aiService) {
        this.taskRepository = taskRepository;
        this.aiService = aiService;
    }

    @Override
    public AnalyticsResponse getTaskStatistics() {
        long totalTasks = taskRepository.count();
        long todoTasks = taskRepository.countByStatus(TaskStatus.TODO);
        long inProgressTasks = taskRepository.countByStatus(TaskStatus.IN_PROGRESS);
        long doneTasks = taskRepository.countByStatus(TaskStatus.DONE);
        long highPriorityTasks = taskRepository.countByPriority(TaskPriority.HIGH);
        long overdueTasks = taskRepository.countByDueDateBeforeAndStatusNot(
                LocalDateTime.now(),
                TaskStatus.DONE
        );

        return new AnalyticsResponse(
                totalTasks,
                todoTasks,
                inProgressTasks,
                doneTasks,
                highPriorityTasks,
                overdueTasks
        );
    }

    @Override
    public AnalyticsResponse getCurrentUserTaskStatistics() {
        String email = getCurrentUserEmail();

        long totalTasks = taskRepository.countByUserEmail(email);
        long todoTasks = taskRepository.countByUserEmailAndStatus(email, TaskStatus.TODO);
        long inProgressTasks = taskRepository.countByUserEmailAndStatus(email, TaskStatus.IN_PROGRESS);
        long doneTasks = taskRepository.countByUserEmailAndStatus(email, TaskStatus.DONE);
        long highPriorityTasks = taskRepository.countByUserEmailAndPriority(email, TaskPriority.HIGH);
        long overdueTasks = taskRepository.countByUserEmailAndDueDateBeforeAndStatusNot(
                email,
                LocalDateTime.now(),
                TaskStatus.DONE
        );

        return new AnalyticsResponse(
                totalTasks,
                todoTasks,
                inProgressTasks,
                doneTasks,
                highPriorityTasks,
                overdueTasks
        );
    }

    @Override
    public UserDashboardResponse getCurrentUserDashboard() {
        String email = getCurrentUserEmail();

        AnalyticsResponse analytics = getCurrentUserTaskStatistics();
        AiRecommendationResponse recommendation = null;

        try {
            recommendation = aiService.recommendNextBestTaskForCurrentUser();
        } catch (BusinessException ignored) {
        }

        UserDashboardResponse response = new UserDashboardResponse();
        response.setUserEmail(email);
        response.setAnalytics(analytics);
        response.setRecommendation(recommendation);
        response.setSummary(buildSummary(analytics, recommendation));

        return response;
    }

    private String buildSummary(AnalyticsResponse analytics, AiRecommendationResponse recommendation) {
        StringBuilder summary = new StringBuilder();
        summary.append("Vous avez ")
                .append(analytics.getTotalTasks())
                .append(" tâche(s), dont ")
                .append(analytics.getTodoTasks())
                .append(" à faire et ")
                .append(analytics.getOverdueTasks())
                .append(" en retard.");

        if (recommendation != null) {
            summary.append(" La prochaine meilleure tâche recommandée est : ")
                    .append(recommendation.getTitle())
                    .append(".");
        } else {
            summary.append(" Aucune recommandation n'est disponible pour le moment.");
        }

        return summary.toString();
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException("Authenticated user not found");
        }

        return authentication.getName();
    }
}