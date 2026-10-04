package com.taskai.optimizer.services;

import com.taskai.optimizer.entity.User;

public interface SmartNotificationService {

    void generateTaskRecommendationNotification(User user);

    void generateOverloadNotification(User user);
}