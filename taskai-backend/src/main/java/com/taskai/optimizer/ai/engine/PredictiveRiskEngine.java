package com.taskai.optimizer.ai.engine;

import com.taskai.optimizer.ai.model.BehaviorProfile;
import com.taskai.optimizer.ai.model.PredictionResult;
import com.taskai.optimizer.entity.Task;
import com.taskai.optimizer.enums.TaskPriority;
import com.taskai.optimizer.enums.TaskStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class PredictiveRiskEngine {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");

    private final UserBehaviorLearningEngine learningEngine;

    public PredictiveRiskEngine(UserBehaviorLearningEngine learningEngine) {
        this.learningEngine = learningEngine;
    }

    public PredictionResult predict(Task task) {
        BehaviorProfile profile = task.getUser() != null
                ? learningEngine.buildProfile(task.getUser().getEmail())
                : new BehaviorProfile(0, 24.0, 0.5, 0.0, "UNKNOWN");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime baseStart = task.getStartedAt() != null ? task.getStartedAt() : now;

        double expectedHours = estimateExpectedHours(task, profile);
        LocalDateTime predictedCompletionAt = baseStart.plusMinutes((long) (expectedHours * 60));

        double delayProbability = estimateDelayProbability(task, profile, predictedCompletionAt);
        double completionProbability = Math.max(5.0, 100.0 - delayProbability + completionBoost(task));
        double dynamicRiskScore = Math.min(100.0, delayProbability * 0.75 + priorityBoost(task));
        double confidenceScore = profile.getCompletedTasksCount() >= 5 ? 82.0 : 60.0;

        String rationale = buildRationale(profile, predictedCompletionAt, task);

        return new PredictionResult(
                round(delayProbability),
                round(Math.min(completionProbability, 100.0)),
                round(dynamicRiskScore),
                confidenceScore,
                predictedCompletionAt,
                rationale
        );
    }

    private double estimateExpectedHours(Task task, BehaviorProfile profile) {
        double baseline = switch (task.getPriority()) {
            case HIGH -> 12.0;
            case MEDIUM -> 24.0;
            case LOW -> 36.0;
            default -> 24.0;
        };

        double behavior = profile.getAverageCompletionHours() > 0 ? profile.getAverageCompletionHours() : baseline;
        double blended = (baseline * 0.4) + (behavior * 0.6);

        if (task.getStatus() == TaskStatus.IN_PROGRESS) {
            blended *= 0.7;
        }

        if ("SLOW".equals(profile.getSpeedCategory())) {
            blended *= 1.15;
        } else if ("FAST".equals(profile.getSpeedCategory())) {
            blended *= 0.85;
        }

        return Math.max(4.0, blended);
    }

    private double estimateDelayProbability(Task task,
                                            BehaviorProfile profile,
                                            LocalDateTime predictedCompletionAt) {
        double score = 10.0;

        if (task.getDueDate() == null) {
            score += 10.0;
        } else {
            if (predictedCompletionAt.isAfter(task.getDueDate())) {
                score += 45.0;
            } else if (predictedCompletionAt.isAfter(task.getDueDate().minusDays(1))) {
                score += 22.0;
            }

            if (task.getDueDate().isBefore(LocalDateTime.now())) {
                score += 25.0;
            }
        }

        score += profile.getOverdueRate() * 25.0;
        score -= profile.getOnTimeCompletionRate() * 12.0;

        if (task.getPriority() == TaskPriority.HIGH) {
            score += 8.0;
        }

        if (task.getStatus() == TaskStatus.TODO) {
            score += 8.0;
        }

        if (task.getUser() == null) {
            score += 12.0;
        }

        return clamp(score);
    }

    private double completionBoost(Task task) {
        if (task.getStatus() == TaskStatus.IN_PROGRESS) {
            return 8.0;
        }
        if (task.getStatus() == TaskStatus.DONE) {
            return 20.0;
        }
        return 0.0;
    }

    private double priorityBoost(Task task) {
        return switch (task.getPriority()) {
            case HIGH -> 15.0;
            case MEDIUM -> 8.0;
            case LOW -> 2.0;
            default -> 0.0;
        };
    }

    private String buildRationale(BehaviorProfile profile,
                                  LocalDateTime predictedCompletionAt,
                                  Task task) {
        String speedLabel = switch (profile.getSpeedCategory()) {
            case "FAST" -> "historique d'exécution rapide";
            case "SLOW" -> "historique d'exécution plus lent";
            default -> "historique d'exécution modérément stable";
        };

        return "Prédiction établie à partir du comportement utilisateur ("
                + speedLabel
                + "), de la priorité "
                + toFrenchPriority(task.getPriority())
                + ", du statut "
                + toFrenchStatus(task.getStatus())
                + " et d'une complétion estimée au "
                + predictedCompletionAt.format(DATE_FORMATTER)
                + ".";
    }

    private String toFrenchPriority(TaskPriority priority) {
        if (priority == null) {
            return "inconnue";
        }

        return switch (priority) {
            case HIGH -> "élevée";
            case MEDIUM -> "moyenne";
            case LOW -> "faible";
        };
    }

    private String toFrenchStatus(TaskStatus status) {
        if (status == null) {
            return "inconnu";
        }

        return switch (status) {
            case TODO -> "À faire";
            case IN_PROGRESS -> "En cours";
            case DONE -> "Terminée";
        };
    }

    private double clamp(double value) {
        return Math.max(0.0, Math.min(100.0, value));
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}