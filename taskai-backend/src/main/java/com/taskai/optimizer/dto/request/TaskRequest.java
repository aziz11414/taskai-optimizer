package com.taskai.optimizer.dto.request;

import com.taskai.optimizer.enums.TaskPriority;
import com.taskai.optimizer.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class TaskRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 255, message = "Title must be between 2 and 255 characters")
    public String title;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    public String description;

    @NotNull(message = "Status is required")
    public TaskStatus status;

    @NotNull(message = "Priority is required")
    public TaskPriority priority;

    public LocalDateTime dueDate;

    public Long userId;
}