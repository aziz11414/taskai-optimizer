package com.taskai.optimizer.ai.rules;

import com.taskai.optimizer.entity.Task;
import com.taskai.optimizer.enums.TaskPriority;
import com.taskai.optimizer.enums.TaskStatus;

import java.time.LocalDateTime;

public final class PriorityRules {

    private PriorityRules() {
    }

    public static double calculate(Task task) {
        if (task == null) {
            return 0.0;
        }

        double score = 0.0;

        score += scoreByPriority(task.getPriority());
        score += scoreByStatus(task.getStatus());
        score += scoreByDueDate(task);

        return Math.min(score, 100.0);
    }

    private static double scoreByPriority(TaskPriority priority) {
        if (priority == null) {
            return 0.0;
        }

        return switch (priority) {
            case HIGH -> 50.0;
            case MEDIUM -> 30.0;
            case LOW -> 10.0;
        };
    }

    private static double scoreByStatus(TaskStatus status) {
        if (status == null) {
            return 0.0;
        }

        return switch (status) {
            case TODO -> 20.0;
            case IN_PROGRESS -> 10.0;
            case DONE -> 0.0;
        };
    }

    private static double scoreByDueDate(Task task) {
        if (task.getDueDate() == null) {
            return 0.0;
        }

        LocalDateTime now = LocalDateTime.now();

        if (task.getDueDate().isBefore(now)) {
            return 30.0;
        }
        if (task.getDueDate().isBefore(now.plusDays(1))) {
            return 25.0;
        }
        if (task.getDueDate().isBefore(now.plusDays(3))) {
            return 15.0;
        }
        if (task.getDueDate().isBefore(now.plusDays(7))) {
            return 5.0;
        }

        return 0.0;
    }
}