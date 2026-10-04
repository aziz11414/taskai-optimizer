package com.taskai.optimizer.ai.engine;

import com.taskai.optimizer.ai.model.ExplanationResult;
import com.taskai.optimizer.ai.model.ScoreResult;
import com.taskai.optimizer.entity.Task;
import com.taskai.optimizer.enums.TaskPriority;
import com.taskai.optimizer.enums.TaskStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExplainabilityEngine {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");

    public ExplanationResult explain(Task task, ScoreResult scoreResult) {
        List<String> reasons = new ArrayList<>();

        if (task.getPriority() == TaskPriority.HIGH) {
            reasons.add("La tâche est marquée comme haute priorité.");
        } else if (task.getPriority() == TaskPriority.MEDIUM) {
            reasons.add("La tâche possède une priorité moyenne.");
        } else if (task.getPriority() == TaskPriority.LOW) {
            reasons.add("La tâche possède une priorité faible.");
        }

        if (task.getStatus() == TaskStatus.TODO) {
            reasons.add("La tâche n'a pas encore commencé.");
        } else if (task.getStatus() == TaskStatus.IN_PROGRESS) {
            reasons.add("La tâche est déjà en cours, ce qui améliore sa probabilité d'achèvement.");
        } else if (task.getStatus() == TaskStatus.DONE) {
            reasons.add("La tâche est déjà terminée.");
        }

        if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDateTime.now())) {
            reasons.add("La date limite est déjà dépassée.");
        } else if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDateTime.now().plusDays(2))) {
            reasons.add("La date limite est très proche.");
        }

        if (task.getUser() == null) {
            reasons.add("Aucun utilisateur n'est assigné à cette tâche.");
        } else {
            reasons.add("La tâche est assignée à " + task.getUser().getFullName() + ".");
        }

        reasons.add("Probabilité estimée de retard : " + formatPercentage(scoreResult.getDelayProbability()) + ".");
        reasons.add("Probabilité estimée de complétion : " + formatPercentage(scoreResult.getCompletionProbability()) + ".");

        if (scoreResult.getPredictedCompletionAt() != null) {
            reasons.add("Date de complétion prédite : " + scoreResult.getPredictedCompletionAt().format(DATE_FORMATTER) + ".");
        }

        if (scoreResult.getPredictionRationale() != null && !scoreResult.getPredictionRationale().isBlank()) {
            reasons.add(scoreResult.getPredictionRationale());
        }

        String summary = "Cette tâche présente un niveau de priorité "
                + toFrenchLevel(scoreResult.getPriorityLevel())
                + ", un niveau de risque "
                + toFrenchLevel(scoreResult.getRiskLevel())
                + " et une probabilité de retard de "
                + formatPercentage(scoreResult.getDelayProbability())
                + ".";

        return new ExplanationResult(summary, reasons);
    }

    private String toFrenchLevel(String level) {
        if (level == null) {
            return "inconnu";
        }

        return switch (level) {
            case "HIGH" -> "élevé";
            case "MEDIUM" -> "moyen";
            case "LOW" -> "faible";
            default -> level;
        };
    }

    private String formatPercentage(double value) {
        return String.format("%.1f%%", value).replace(".0%", "%");
    }
}