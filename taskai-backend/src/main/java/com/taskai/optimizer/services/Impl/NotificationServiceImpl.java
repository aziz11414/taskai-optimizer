package com.taskai.optimizer.services.Impl;

import com.taskai.optimizer.dto.request.NotificationRequest;
import com.taskai.optimizer.dto.response.NotificationResponse;
import com.taskai.optimizer.entity.Notification;
import com.taskai.optimizer.entity.User;
import com.taskai.optimizer.enums.NotificationType;
import com.taskai.optimizer.exception.ResourceNotFoundException;
import com.taskai.optimizer.mapper.NotificationMapper;
import com.taskai.optimizer.repository.NotificationRepository;
import com.taskai.optimizer.repository.UserRepository;
import com.taskai.optimizer.services.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public NotificationResponse create(NotificationRequest request) {
        User user = userRepository.findById(request.userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + request.userId + " not found"));

        return createForUser(user, request.title.trim(), request.message.trim(), request.type, null, null);
    }

    @Override
    @Transactional
    public NotificationResponse createForUser(User user, String title, String message, NotificationType type) {
        return createForUser(user, title, message, type, null, null);
    }

    @Override
    @Transactional
    public NotificationResponse createForUser(User user,
                                              String title,
                                              String message,
                                              NotificationType type,
                                              Long relatedTaskId,
                                              String actionUrl) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setUser(user);
        notification.setRelatedTaskId(relatedTaskId);
        notification.setActionUrl(actionUrl);

        Notification saved = notificationRepository.save(notification);
        return NotificationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public List<NotificationResponse> getMyNotifications() {
        String email = getCurrentUserEmail();

        return notificationRepository.findByUserEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(NotificationMapper::toResponse)
                .toList();
    }

    @Override
    public long countUnread() {
        return notificationRepository.countByUserEmailAndIsReadFalse(getCurrentUserEmail());
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long id) {
        String email = getCurrentUserEmail();

        Notification notification = notificationRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found for current user"));

        if (!notification.isRead()) {
            notification.setRead(true);
            notification = notificationRepository.save(notification);
        }

        return NotificationMapper.toResponse(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        notificationRepository.markAllAsReadByUserEmail(getCurrentUserEmail());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        String email = getCurrentUserEmail();

        long deletedCount = notificationRepository.deleteByIdAndUserEmail(id, email);
        if (deletedCount == 0) {
            throw new ResourceNotFoundException("Notification not found for current user");
        }
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new ResourceNotFoundException("Authenticated user not found");
        }

        return authentication.getName();
    }
}