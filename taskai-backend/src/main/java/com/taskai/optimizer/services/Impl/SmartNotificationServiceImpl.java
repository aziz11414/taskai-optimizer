package com.taskai.optimizer.services.Impl;

import com.taskai.optimizer.entity.Task;
import com.taskai.optimizer.entity.User;
import com.taskai.optimizer.enums.NotificationType;
import com.taskai.optimizer.enums.TaskPriority;
import com.taskai.optimizer.enums.TaskStatus;
import com.taskai.optimizer.repository.TaskRepository;
import com.taskai.optimizer.services.NotificationService;
import com.taskai.optimizer.services.SmartNotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class SmartNotificationServiceImpl implements SmartNotificationService {

    private final TaskRepository taskRepository;
    private final NotificationService notificationService;

    public SmartNotificationServiceImpl(TaskRepository taskRepository,
                                        NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.notificationService = notificationService;
    }

    @Override
    public void generateTaskRecommendationNotification(User user) {
        List<Task> tasks = taskRepository.findByUserEmail(user.getEmail());

        LocalDateTime soon = LocalDateTime.now().plusDays(2);

        Task recommendedTask = tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .filter(task -> task.getPriority() == TaskPriority.HIGH)
                .filter(task -> task.getDueDate() != null)
                .filter(task -> !task.getDueDate().isAfter(soon))
                .sorted(Comparator.comparing(Task::getDueDate))
                .findFirst()
                .orElse(null);

        if (recommendedTask == null) {
            return;
        }

        notificationService.createForUser(
                user,
                "Action recommandée",
                "Vous devriez traiter la tâche \"" + recommendedTask.getTitle()
                        + "\" maintenant. Elle est prioritaire et proche de son échéance.",
                NotificationType.WARNING
        );
    }

    @Override
    public void generateOverloadNotification(User user) {
        String email = user.getEmail();
        LocalDateTime now = LocalDateTime.now();

        List<Task> activeTasks = taskRepository.findByUserEmailAndStatusNot(email, TaskStatus.DONE);

        long activeCount = activeTasks.size();
        long highPriorityCount = activeTasks.stream()
                .filter(task -> task.getPriority() == TaskPriority.HIGH)
                .count();

        long overdueCount = activeTasks.stream()
                .filter(task -> task.getDueDate() != null && task.getDueDate().isBefore(now))
                .count();

        boolean overloaded = activeCount >= 6 || highPriorityCount >= 3 || overdueCount >= 2;

        if (!overloaded) {
            return;
        }

        notificationService.createForUser(
                user,
                "Alerte de charge",
                "Vous êtes actuellement surchargé. Vous avez "
                        + activeCount + " tâche(s) active(s), dont "
                        + highPriorityCount + " prioritaire(s) et "
                        + overdueCount + " en retard.",
                NotificationType.ERROR
        );
    }
}