package com.taskai.optimizer.ai.engine;

import com.taskai.optimizer.entity.Task;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class TaskRecommendationEngine {

    private final TaskScoringEngine taskScoringEngine;

    public TaskRecommendationEngine(TaskScoringEngine taskScoringEngine) {
        this.taskScoringEngine = taskScoringEngine;
    }

    public Task recommendNextBestTask(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return null;
        }

        return tasks.stream()
                .max(Comparator.comparingDouble((Task task) -> taskScoringEngine.calculateCombinedScore(task)))
                .orElse(null);
    }
}