package com.taskai.optimizer.dto.response;

import com.taskai.optimizer.enums.TaskPriority;
import com.taskai.optimizer.enums.TaskStatus;

import java.time.LocalDateTime;

public class TaskResponse {

    public Long id;
    public String title;
    public String description;
    public TaskStatus status;
    public TaskPriority priority;
    public LocalDateTime dueDate;
    public LocalDateTime createdAt;
    public LocalDateTime startedAt;
    public LocalDateTime completedAt;
    public Double actualDurationHours;

    public Long userId;
    public String userFullName;
    public String userEmail;
}