package com.taskai.optimizer.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_predictions")
public class AiPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double delayProbability;

    @Column(nullable = false)
    private Double completionProbability;

    private LocalDateTime predictedCompletionAt;

    @Column(nullable = false)
    private Double confidenceScore;

    @Column(nullable = false)
    private String modelVersion;

    @Column(length = 1000)
    private String rationale;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getDelayProbability() {
        return delayProbability;
    }

    public void setDelayProbability(Double delayProbability) {
        this.delayProbability = delayProbability;
    }

    public Double getCompletionProbability() {
        return completionProbability;
    }

    public void setCompletionProbability(Double completionProbability) {
        this.completionProbability = completionProbability;
    }

    public LocalDateTime getPredictedCompletionAt() {
        return predictedCompletionAt;
    }

    public void setPredictedCompletionAt(LocalDateTime predictedCompletionAt) {
        this.predictedCompletionAt = predictedCompletionAt;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}