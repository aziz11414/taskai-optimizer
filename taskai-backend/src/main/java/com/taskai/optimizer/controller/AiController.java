package com.taskai.optimizer.controller;

import com.taskai.optimizer.dto.response.AiRecommendationListResponse;
import com.taskai.optimizer.dto.response.AiRecommendationResponse;
import com.taskai.optimizer.dto.response.AiTaskAnalysisResponse;
import com.taskai.optimizer.services.AiService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AiController {

    private final AiService service;

    public AiController(AiService service) {
        this.service = service;
    }

    @GetMapping("/tasks/{taskId}/analyze")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public AiTaskAnalysisResponse analyzeTask(@PathVariable Long taskId) {
        return service.analyzeTask(taskId);
    }

    @GetMapping("/tasks/recommendation")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public AiRecommendationResponse recommendNextBestTask() {
        return service.recommendNextBestTaskForCurrentUser();
    }

    @GetMapping("/tasks/recommendations")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public AiRecommendationListResponse recommendTopTasks(
            @RequestParam(defaultValue = "5") int limit) {
        return service.recommendTopTasksForCurrentUser(limit);
    }
}