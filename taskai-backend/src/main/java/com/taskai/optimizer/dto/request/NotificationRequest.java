package com.taskai.optimizer.dto.request;

import com.taskai.optimizer.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NotificationRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 255, message = "Title must be between 2 and 255 characters")
    public String title;

    @NotBlank(message = "Message is required")
    @Size(min = 2, max = 1000, message = "Message must be between 2 and 1000 characters")
    public String message;

    @NotNull(message = "Type is required")
    public NotificationType type;

    @NotNull(message = "User id is required")
    public Long userId;
}