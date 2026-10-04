package com.taskai.optimizer.ai.model;

import java.time.LocalDateTime;

public class ScoreResult {

    private double priorityScore;
    private double riskScore;
    private double combinedScore;
    private double delayProbability;
    private double completionProbability;
    private double confidenceScore;
    private LocalDateTime predictedCompletionAt;
    private String priorityLevel;
    private String riskLevel;
    private String predictionRationale;

    public ScoreResult() {
    }

    public ScoreResult(double priorityScore,
                       double riskScore,
                       double combinedScore,
                       double delayProbability,
                       double completionProbability,
                       double confidenceScore,
                       LocalDateTime predictedCompletionAt,
                       String priorityLevel,
                       String riskLevel,
                       String predictionRationale) {
        this.priorityScore = priorityScore;
        this.riskScore = riskScore;
        this.combinedScore = combinedScore;
        this.delayProbability = delayProbability;
        this.completionProbability = completionProbability;
        this.confidenceScore = confidenceScore;
        this.predictedCompletionAt = predictedCompletionAt;
        this.priorityLevel = priorityLevel;
        this.riskLevel = riskLevel;
        this.predictionRationale = predictionRationale;
    }

    public double getPriorityScore() {
        return priorityScore;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public double getCombinedScore() {
        return combinedScore;
    }

    public double getDelayProbability() {
        return delayProbability;
    }

    public double getCompletionProbability() {
        return completionProbability;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public LocalDateTime getPredictedCompletionAt() {
        return predictedCompletionAt;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getPredictionRationale() {
        return predictionRationale;
    }
}