package com.taskai.optimizer.services;

import com.taskai.optimizer.dto.request.NotificationRequest;
import com.taskai.optimizer.dto.response.NotificationResponse;
import com.taskai.optimizer.entity.User;
import com.taskai.optimizer.enums.NotificationType;

import java.util.List;

public interface NotificationService {

    NotificationResponse create(NotificationRequest request);

    NotificationResponse createForUser(User user, String title, String message, NotificationType type);

    NotificationResponse createForUser(
            User user,
            String title,
            String message,
            NotificationType type,
            Long relatedTaskId,
            String actionUrl
    );

    List<NotificationResponse> getMyNotifications();

    long countUnread();

    NotificationResponse markAsRead(Long id);

    void markAllAsRead();

    void delete(Long id);
}