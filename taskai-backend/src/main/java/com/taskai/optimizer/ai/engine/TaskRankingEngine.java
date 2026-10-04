package com.taskai.optimizer.ai.engine;

import com.taskai.optimizer.entity.Task;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class TaskRankingEngine {

    private final TaskScoringEngine taskScoringEngine;

    public TaskRankingEngine(TaskScoringEngine taskScoringEngine) {
        this.taskScoringEngine = taskScoringEngine;
    }

    public List<Task> rankTasks(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return List.of();
        }

        return tasks.stream()
                .sorted(Comparator.comparingDouble((Task task) -> taskScoringEngine.calculateCombinedScore(task)).reversed())
                .toList();
    }

    public double combinedScore(Task task) {
        return taskScoringEngine.calculateCombinedScore(task);
    }
}