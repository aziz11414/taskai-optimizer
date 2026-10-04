package com.taskai.optimizer.ai.engine;

import com.taskai.optimizer.ai.model.BehaviorProfile;
import com.taskai.optimizer.entity.Task;
import com.taskai.optimizer.enums.TaskStatus;
import com.taskai.optimizer.repository.TaskRepository;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class UserBehaviorLearningEngine {

    private final TaskRepository taskRepository;

    public UserBehaviorLearningEngine(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public BehaviorProfile buildProfile(String email) {
        List<Task> allTasks = taskRepository.findByUserEmail(email);

        List<Task> completedTasks = allTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .filter(task -> task.getCompletedAt() != null)
                .toList();

        if (completedTasks.isEmpty()) {
            return new BehaviorProfile(0, 24.0, 0.5, 0.0, "UNKNOWN");
        }

        double averageCompletionHours = completedTasks.stream()
                .mapToDouble(task -> {
                    if (task.getActualDurationHours() != null) {
                        return task.getActualDurationHours();
                    }

                    return Duration.between(
                            task.getStartedAt() != null ? task.getStartedAt() : task.getCreatedAt(),
                            task.getCompletedAt()
                    ).toMinutes() / 60.0;
                })
                .average()
                .orElse(24.0);

        long onTimeCount = completedTasks.stream()
                .filter(task -> task.getDueDate() == null || !task.getCompletedAt().isAfter(task.getDueDate()))
                .count();

        long overdueCount = completedTasks.stream()
                .filter(task -> task.getDueDate() != null && task.getCompletedAt().isAfter(task.getDueDate()))
                .count();

        double onTimeRate = completedTasks.isEmpty() ? 0.5 : (double) onTimeCount / completedTasks.size();
        double overdueRate = completedTasks.isEmpty() ? 0.0 : (double) overdueCount / completedTasks.size();

        String speedCategory;
        if (averageCompletionHours <= 12) {
            speedCategory = "FAST";
        } else if (averageCompletionHours <= 36) {
            speedCategory = "NORMAL";
        } else {
            speedCategory = "SLOW";
        }

        return new BehaviorProfile(
                completedTasks.size(),
                averageCompletionHours,
                onTimeRate,
                overdueRate,
                speedCategory
        );
    }
}