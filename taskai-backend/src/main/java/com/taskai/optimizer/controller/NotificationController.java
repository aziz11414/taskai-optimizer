package com.taskai.optimizer.controller;

import com.taskai.optimizer.dto.request.NotificationRequest;
import com.taskai.optimizer.dto.response.NotificationResponse;
import com.taskai.optimizer.services.NotificationService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public NotificationResponse create(@Valid @RequestBody NotificationRequest request) {
        return notificationService.create(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MANAGER')")
    public List<NotificationResponse> getMyNotifications() {
        return notificationService.getMyNotifications();
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MANAGER')")
    public Map<String, Long> countUnread() {
        return Map.of("count", notificationService.countUnread());
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MANAGER')")
    public NotificationResponse markAsRead(@PathVariable Long id) {
        return notificationService.markAsRead(id);
    }

    @PutMapping("/read-all")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MANAGER')")
    public Map<String, String> markAllAsRead() {
        notificationService.markAllAsRead();
        return Map.of("message", "Toutes les notifications ont été marquées comme lues");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MANAGER')")
    public Map<String, String> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return Map.of("message", "Notification supprimée avec succès");
    }
}