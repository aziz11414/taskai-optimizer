package com.taskai.optimizer.mapper;

import com.taskai.optimizer.dto.response.NotificationResponse;
import com.taskai.optimizer.entity.Notification;

public class NotificationMapper {

    private NotificationMapper() {
    }

    public static NotificationResponse toResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.id = notification.getId();
        response.title = notification.getTitle();
        response.message = notification.getMessage();
        response.type = notification.getType() != null ? notification.getType().name() : null;
        response.isRead = notification.isRead();
        response.createdAt = notification.getCreatedAt();

        if (notification.getUser() != null) {
            response.userId = notification.getUser().getId();
            response.userFullName = notification.getUser().getFullName();
            response.userEmail = notification.getUser().getEmail();
        }

        response.relatedTaskId = notification.getRelatedTaskId();
        response.actionUrl = notification.getActionUrl();

        return response;
    }
}