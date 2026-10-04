package com.taskai.optimizer.ai.rules;

import com.taskai.optimizer.entity.Task;
import com.taskai.optimizer.enums.TaskPriority;
import com.taskai.optimizer.enums.TaskStatus;

import java.time.LocalDateTime;

public final class RiskRules {

    private RiskRules() {
    }

    public static double calculate(Task task) {
        if (task == null) {
            return 0.0;
        }

        double score = 0.0;

        score += scoreByDueDate(task);
        score += scoreByStatus(task.getStatus());
        score += scoreByPriority(task.getPriority());
        score += scoreByAssignment(task);

        return Math.min(score, 100.0);
    }

    private static double scoreByDueDate(Task task) {
        if (task.getDueDate() == null) {
            return 0.0;
        }

        LocalDateTime now = LocalDateTime.now();

        if (task.getDueDate().isBefore(now)) {
            return 60.0;
        }
        if (task.getDueDate().isBefore(now.plusDays(1))) {
            return 35.0;
        }
        if (task.getDueDate().isBefore(now.plusDays(3))) {
            return 20.0;
        }

        return 0.0;
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

    private static double scoreByPriority(TaskPriority priority) {
        if (priority == null) {
            return 0.0;
        }

        return switch (priority) {
            case HIGH -> 15.0;
            case MEDIUM -> 8.0;
            case LOW -> 0.0;
        };
    }

    private static double scoreByAssignment(Task task) {
        return task.getUser() == null ? 10.0 : 0.0;
    }
}