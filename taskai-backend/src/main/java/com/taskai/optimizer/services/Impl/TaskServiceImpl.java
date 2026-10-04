package com.taskai.optimizer.services.Impl;

import com.taskai.optimizer.dto.request.TaskRequest;
import com.taskai.optimizer.dto.response.TaskResponse;
import com.taskai.optimizer.entity.AuditLog;
import com.taskai.optimizer.entity.Task;
import com.taskai.optimizer.entity.User;
import com.taskai.optimizer.enums.NotificationType;
import com.taskai.optimizer.enums.TaskPriority;
import com.taskai.optimizer.enums.TaskStatus;
import com.taskai.optimizer.exception.ResourceNotFoundException;
import com.taskai.optimizer.repository.AuditLogRepository;
import com.taskai.optimizer.repository.TaskRepository;
import com.taskai.optimizer.repository.UserRepository;
import com.taskai.optimizer.services.NotificationService;
import com.taskai.optimizer.services.SmartNotificationService;
import com.taskai.optimizer.services.TaskService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final SmartNotificationService smartNotificationService;
    private final AuditLogRepository auditLogRepository;

    public TaskServiceImpl(TaskRepository repository,
                           UserRepository userRepository,
                           NotificationService notificationService,
                           SmartNotificationService smartNotificationService,
                           AuditLogRepository auditLogRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.smartNotificationService = smartNotificationService;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional
    public TaskResponse create(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.title);
        task.setDescription(request.description);
        task.setStatus(request.status);
        task.setPriority(request.priority);
        task.setDueDate(request.dueDate);

        User assignedUser = resolveAssignedUser(request.userId);
        task.setUser(assignedUser);

        if (task.getStatus() == TaskStatus.IN_PROGRESS) {
            task.markStartedIfNeeded();
        } else if (task.getStatus() == TaskStatus.DONE) {
            task.markStartedIfNeeded();
            task.markCompletedIfNeeded();
        }

        Task saved = repository.save(task);

        logAction(
                assignedUser,
                "TASK_CREATED",
                "TASK",
                saved.getId(),
                "Tâche créée : " + saved.getTitle()
        );

        notificationService.createForUser(
                assignedUser,
                "Nouvelle tâche assignée",
                "Une nouvelle tâche \"" + saved.getTitle() + "\" vous a été assignée.",
                NotificationType.INFO
        );

        if (saved.getPriority() == TaskPriority.HIGH) {
            notificationService.createForUser(
                    assignedUser,
                    "Tâche haute priorité",
                    "La tâche \"" + saved.getTitle() + "\" est marquée comme prioritaire.",
                    NotificationType.WARNING
            );
        }

        smartNotificationService.generateTaskRecommendationNotification(assignedUser);
        smartNotificationService.generateOverloadNotification(assignedUser);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAll() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        boolean isAdmin = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        List<Task> tasks = isAdmin
                ? repository.findAll()
                : repository.findByUserEmail(email);

        return tasks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getById(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));

        return mapToResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse update(Long id, TaskRequest request) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));

        TaskStatus previousStatus = task.getStatus();

        task.setTitle(request.title);
        task.setDescription(request.description);
        task.setStatus(request.status);
        task.setPriority(request.priority);
        task.setDueDate(request.dueDate);

        if (request.userId != null) {
            User user = userRepository.findById(request.userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            task.setUser(user);
        }

        if (previousStatus != TaskStatus.IN_PROGRESS && task.getStatus() == TaskStatus.IN_PROGRESS) {
            task.markStartedIfNeeded();
        }

        if (previousStatus != TaskStatus.DONE && task.getStatus() == TaskStatus.DONE) {
            task.markStartedIfNeeded();
            task.markCompletedIfNeeded();
        }

        if (previousStatus == TaskStatus.DONE && task.getStatus() != TaskStatus.DONE) {
            task.resetCompletionTracking();
        }

        Task updated = repository.save(task);

        if (updated.getUser() != null) {
            logAction(
                    updated.getUser(),
                    "TASK_UPDATED",
                    "TASK",
                    updated.getId(),
                    "Tâche mise à jour : " + updated.getTitle()
            );

            notificationService.createForUser(
                    updated.getUser(),
                    "Tâche mise à jour",
                    "La tâche \"" + updated.getTitle() + "\" a été mise à jour.",
                    NotificationType.INFO
            );

            if (updated.getPriority() == TaskPriority.HIGH) {
                notificationService.createForUser(
                        updated.getUser(),
                        "Priorité élevée confirmée",
                        "La tâche \"" + updated.getTitle() + "\" reste en priorité élevée.",
                        NotificationType.WARNING
                );
            }

            if (updated.getStatus() == TaskStatus.DONE) {
                notificationService.createForUser(
                        updated.getUser(),
                        "Tâche terminée",
                        "Bravo, la tâche \"" + updated.getTitle() + "\" a été marquée comme terminée.",
                        NotificationType.SUCCESS
                );
            }

            smartNotificationService.generateTaskRecommendationNotification(updated.getUser());
            smartNotificationService.generateOverloadNotification(updated.getUser());
        }

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found");
        }
        repository.deleteById(id);
    }

    private User resolveAssignedUser(Long userId) {
        if (userId != null) {
            return userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    private void logAction(User user, String actionType, String entityType, Long entityId, String details) {
        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setActionType(actionType);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDetails(details);
        auditLogRepository.save(log);
    }

    private TaskResponse mapToResponse(Task task) {
        TaskResponse res = new TaskResponse();
        res.id = task.getId();
        res.title = task.getTitle();
        res.description = task.getDescription();
        res.status = task.getStatus();
        res.priority = task.getPriority();
        res.dueDate = task.getDueDate();
        res.createdAt = task.getCreatedAt();
        res.startedAt = task.getStartedAt();
        res.completedAt = task.getCompletedAt();
        res.actualDurationHours = task.getActualDurationHours();

        if (task.getUser() != null) {
            res.userId = task.getUser().getId();
            res.userFullName = task.getUser().getFullName();
            res.userEmail = task.getUser().getEmail();
        }

        return res;
    }
}