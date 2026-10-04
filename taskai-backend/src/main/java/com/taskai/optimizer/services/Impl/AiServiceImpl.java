package com.taskai.optimizer.services.Impl;

import com.taskai.optimizer.ai.engine.ExplainabilityEngine;
import com.taskai.optimizer.ai.engine.TaskRankingEngine;
import com.taskai.optimizer.ai.engine.TaskRecommendationEngine;
import com.taskai.optimizer.ai.engine.TaskScoringEngine;
import com.taskai.optimizer.ai.model.ExplanationResult;
import com.taskai.optimizer.ai.model.ScoreResult;
import com.taskai.optimizer.dto.response.AiRecommendationListResponse;
import com.taskai.optimizer.dto.response.AiRecommendationResponse;
import com.taskai.optimizer.dto.response.AiTaskAnalysisResponse;
import com.taskai.optimizer.entity.AiPrediction;
import com.taskai.optimizer.entity.Task;
import com.taskai.optimizer.enums.TaskStatus;
import com.taskai.optimizer.exception.BusinessException;
import com.taskai.optimizer.exception.ResourceNotFoundException;
import com.taskai.optimizer.repository.AiPredictionRepository;
import com.taskai.optimizer.repository.TaskRepository;
import com.taskai.optimizer.services.AiService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AiServiceImpl implements AiService {

    private final TaskRepository taskRepository;
    private final TaskScoringEngine taskScoringEngine;
    private final ExplainabilityEngine explainabilityEngine;
    private final TaskRecommendationEngine taskRecommendationEngine;
    private final TaskRankingEngine taskRankingEngine;
    private final AiPredictionRepository aiPredictionRepository;

    public AiServiceImpl(TaskRepository taskRepository,
                         TaskScoringEngine taskScoringEngine,
                         ExplainabilityEngine explainabilityEngine,
                         TaskRecommendationEngine taskRecommendationEngine,
                         TaskRankingEngine taskRankingEngine,
                         AiPredictionRepository aiPredictionRepository) {
        this.taskRepository = taskRepository;
        this.taskScoringEngine = taskScoringEngine;
        this.explainabilityEngine = explainabilityEngine;
        this.taskRecommendationEngine = taskRecommendationEngine;
        this.taskRankingEngine = taskRankingEngine;
        this.aiPredictionRepository = aiPredictionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AiTaskAnalysisResponse analyzeTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + taskId + " not found"));

        ScoreResult scoreResult = taskScoringEngine.score(task);
        ExplanationResult explanationResult = explainabilityEngine.explain(task, scoreResult);

        AiTaskAnalysisResponse response = new AiTaskAnalysisResponse();
        response.setTaskId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus() != null ? task.getStatus().name() : null);
        response.setPriority(task.getPriority() != null ? task.getPriority().name() : null);
        response.setDueDate(task.getDueDate());
        response.setAssignedUser(task.getUser() != null ? task.getUser().getFullName() : null);
        response.setPriorityScore(scoreResult.getPriorityScore());
        response.setRiskScore(scoreResult.getRiskScore());
        response.setCombinedScore(scoreResult.getCombinedScore());
        response.setDelayProbability(scoreResult.getDelayProbability());
        response.setCompletionProbability(scoreResult.getCompletionProbability());
        response.setConfidenceScore(scoreResult.getConfidenceScore());
        response.setPredictedCompletionAt(scoreResult.getPredictedCompletionAt());
        response.setPriorityLevel(scoreResult.getPriorityLevel());
        response.setRiskLevel(scoreResult.getRiskLevel());
        response.setSummary(explanationResult.getSummary());
        response.setReasons(explanationResult.getReasons());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public AiRecommendationResponse recommendNextBestTaskForCurrentUser() {
        String email = getCurrentUserEmail();
        List<Task> tasks = taskRepository.findByUserEmailAndStatusNot(email, TaskStatus.DONE);

        if (tasks.isEmpty()) {
            return null;
        }

        Task recommendedTask = taskRecommendationEngine.recommendNextBestTask(tasks);
        return recommendedTask != null ? mapRecommendation(recommendedTask) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public AiRecommendationListResponse recommendTopTasksForCurrentUser(int limit) {
        String email = getCurrentUserEmail();
        List<Task> tasks = taskRepository.findByUserEmailAndStatusNot(email, TaskStatus.DONE);

        int safeLimit = Math.max(1, Math.min(limit, 10));

        List<AiRecommendationResponse> recommendations = tasks.isEmpty()
                ? List.of()
                : taskRankingEngine.rankTasks(tasks).stream()
                .limit(safeLimit)
                .map(this::mapRecommendation)
                .toList();

        AiRecommendationListResponse response = new AiRecommendationListResponse();
        response.setUserEmail(email);
        response.setTotalCandidates(tasks.size());
        response.setRecommendations(recommendations);

        return response;
    }

    private AiRecommendationResponse mapRecommendation(Task task) {
        ScoreResult scoreResult = taskScoringEngine.score(task);
        ExplanationResult explanationResult = explainabilityEngine.explain(task, scoreResult);

        AiRecommendationResponse response = new AiRecommendationResponse();
        response.setTaskId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus() != null ? task.getStatus().name() : null);
        response.setPriority(task.getPriority() != null ? task.getPriority().name() : null);
        response.setPriorityScore(scoreResult.getPriorityScore());
        response.setRiskScore(scoreResult.getRiskScore());
        response.setCombinedScore(scoreResult.getCombinedScore());
        response.setDelayProbability(scoreResult.getDelayProbability());
        response.setCompletionProbability(scoreResult.getCompletionProbability());
        response.setConfidenceScore(scoreResult.getConfidenceScore());
        response.setPredictedCompletionAt(scoreResult.getPredictedCompletionAt());
        response.setPriorityLevel(scoreResult.getPriorityLevel());
        response.setRiskLevel(scoreResult.getRiskLevel());
        response.setAssignedUser(task.getUser() != null ? task.getUser().getFullName() : null);
        response.setSummary(explanationResult.getSummary());
        response.setReasons(explanationResult.getReasons());

        return response;
    }

    @Transactional
    protected void persistPrediction(Task task, ScoreResult scoreResult) {
        if (task.getUser() == null) {
            return;
        }

        AiPrediction prediction = new AiPrediction();
        prediction.setTask(task);
        prediction.setUser(task.getUser());
        prediction.setDelayProbability(scoreResult.getDelayProbability());
        prediction.setCompletionProbability(scoreResult.getCompletionProbability());
        prediction.setPredictedCompletionAt(scoreResult.getPredictedCompletionAt());
        prediction.setConfidenceScore(scoreResult.getConfidenceScore());
        prediction.setModelVersion("predictive-v2");
        prediction.setRationale(scoreResult.getPredictionRationale());

        aiPredictionRepository.save(prediction);
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException("Authenticated user not found");
        }

        return authentication.getName();
    }
}