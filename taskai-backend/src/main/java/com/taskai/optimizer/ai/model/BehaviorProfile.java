package com.taskai.optimizer.ai.model;

public class BehaviorProfile {

    private long completedTasksCount;
    private double averageCompletionHours;
    private double onTimeCompletionRate;
    private double overdueRate;
    private String speedCategory;

    public BehaviorProfile() {
    }

    public BehaviorProfile(long completedTasksCount,
                           double averageCompletionHours,
                           double onTimeCompletionRate,
                           double overdueRate,
                           String speedCategory) {
        this.completedTasksCount = completedTasksCount;
        this.averageCompletionHours = averageCompletionHours;
        this.onTimeCompletionRate = onTimeCompletionRate;
        this.overdueRate = overdueRate;
        this.speedCategory = speedCategory;
    }

    public long getCompletedTasksCount() {
        return completedTasksCount;
    }

    public double getAverageCompletionHours() {
        return averageCompletionHours;
    }

    public double getOnTimeCompletionRate() {
        return onTimeCompletionRate;
    }

    public double getOverdueRate() {
        return overdueRate;
    }

    public String getSpeedCategory() {
        return speedCategory;
    }
}