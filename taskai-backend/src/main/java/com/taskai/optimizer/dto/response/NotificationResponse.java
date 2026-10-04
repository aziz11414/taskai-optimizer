package com.taskai.optimizer.dto.response;

import java.time.LocalDateTime;

public class NotificationResponse {
    public Long id;
    public String title;
    public String message;
    public String type;
    public boolean isRead;
    public LocalDateTime createdAt;
    public Long userId;
    public String userFullName;
    public String userEmail;
    public Long relatedTaskId;
    public String actionUrl;
}