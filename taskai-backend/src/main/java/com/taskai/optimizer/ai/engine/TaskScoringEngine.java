package com.taskai.optimizer.ai.engine;

import com.taskai.optimizer.ai.model.PredictionResult;
import com.taskai.optimizer.ai.model.ScoreResult;
import com.taskai.optimizer.ai.rules.PriorityRules;
import com.taskai.optimizer.ai.rules.RiskRules;
import com.taskai.optimizer.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskScoringEngine {

    private final PredictiveRiskEngine predictiveRiskEngine;

    public TaskScoringEngine(PredictiveRiskEngine predictiveRiskEngine) {
        this.predictiveRiskEngine = predictiveRiskEngine;
    }

    public ScoreResult score(Task task) {
        double priorityScore = PriorityRules.calculate(task);
        double staticRiskScore = RiskRules.calculate(task);

        PredictionResult prediction = predictiveRiskEngine.predict(task);
        double riskScore = calculateDynamicRiskScore(staticRiskScore, prediction.getDynamicRiskScore());
        double combinedScore = calculateCombinedScore(priorityScore, riskScore);

        return new ScoreResult(
                priorityScore,
                riskScore,
                combinedScore,
                prediction.getDelayProbability(),
                prediction.getCompletionProbability(),
                prediction.getConfidenceScore(),
                prediction.getPredictedCompletionAt(),
                toLevel(priorityScore),
                toLevel(riskScore),
                prediction.getRationale()
        );
    }

    public double calculateCombinedScore(Task task) {
        return score(task).getCombinedScore();
    }

    public double calculateCombinedScore(double priorityScore, double riskScore) {
        return (priorityScore * 0.65) + (riskScore * 0.35);
    }

    private double calculateDynamicRiskScore(double staticRisk, double predictiveRisk) {
        return Math.min(100.0, (staticRisk * 0.55) + (predictiveRisk * 0.45));
    }

    private String toLevel(double score) {
        if (score >= 70.0) {
            return "HIGH";
        }
        if (score >= 40.0) {
            return "MEDIUM";
        }
        return "LOW";
    }
}