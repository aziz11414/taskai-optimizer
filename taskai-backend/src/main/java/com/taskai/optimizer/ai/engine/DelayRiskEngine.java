package com.taskai.optimizer.ai.engine;

import com.taskai.optimizer.entity.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DelayRiskEngine {

    public boolean isDelayed(Task task) {
        return task != null
                && task.getDueDate() != null
                && task.getDueDate().isBefore(LocalDateTime.now());
    }

    public boolean isNearDeadline(Task task) {
        return task != null
                && task.getDueDate() != null
                && task.getDueDate().isAfter(LocalDateTime.now())
                && task.getDueDate().isBefore(LocalDateTime.now().plusDays(2));
    }
}