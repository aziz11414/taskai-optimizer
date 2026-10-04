package com.taskai.optimizer.controller;

import com.taskai.optimizer.dto.response.AnalyticsResponse;
import com.taskai.optimizer.dto.response.ApiResponse;
import com.taskai.optimizer.dto.response.UserDashboardResponse;
import com.taskai.optimizer.services.AnalyticsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin
public class AnalyticsController {

    private final AnalyticsService service;

    public AnalyticsController(AnalyticsService service) {
        this.service = service;
    }

    @GetMapping("/tasks")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ApiResponse<AnalyticsResponse> getGlobalTaskStatistics() {
        return new ApiResponse<>(
                true,
                "Global task analytics fetched successfully",
                service.getTaskStatistics()
        );
    }

    @GetMapping("/my-tasks")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ApiResponse<AnalyticsResponse> getCurrentUserTaskStatistics() {
        return new ApiResponse<>(
                true,
                "Current user task analytics fetched successfully",
                service.getCurrentUserTaskStatistics()
        );
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ApiResponse<UserDashboardResponse> getCurrentUserDashboard() {
        return new ApiResponse<>(
                true,
                "Dashboard fetched successfully",
                service.getCurrentUserDashboard()
        );
    }
}