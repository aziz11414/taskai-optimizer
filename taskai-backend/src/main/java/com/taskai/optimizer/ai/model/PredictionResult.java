package com.taskai.optimizer.ai.model;

import java.time.LocalDateTime;

public class PredictionResult {

    private double delayProbability;
    private double completionProbability;
    private double dynamicRiskScore;
    private double confidenceScore;
    private LocalDateTime predictedCompletionAt;
    private String rationale;

    public PredictionResult() {
    }

    public PredictionResult(double delayProbability,
                            double completionProbability,
                            double dynamicRiskScore,
                            double confidenceScore,
                            LocalDateTime predictedCompletionAt,
                            String rationale) {
        this.delayProbability = delayProbability;
        this.completionProbability = completionProbability;
        this.dynamicRiskScore = dynamicRiskScore;
        this.confidenceScore = confidenceScore;
        this.predictedCompletionAt = predictedCompletionAt;
        this.rationale = rationale;
    }

    public double getDelayProbability() {
        return delayProbability;
    }

    public double getCompletionProbability() {
        return completionProbability;
    }

    public double getDynamicRiskScore() {
        return dynamicRiskScore;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public LocalDateTime getPredictedCompletionAt() {
        return predictedCompletionAt;
    }

    public String getRationale() {
        return rationale;
    }
}