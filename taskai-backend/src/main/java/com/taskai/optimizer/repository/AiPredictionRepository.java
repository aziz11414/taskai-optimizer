package com.taskai.optimizer.repository;

import com.taskai.optimizer.entity.AiPrediction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AiPredictionRepository extends JpaRepository<AiPrediction, Long> {

    Optional<AiPrediction> findTopByTaskIdOrderByCreatedAtDesc(Long taskId);

    List<AiPrediction> findByUserEmailOrderByCreatedAtDesc(String email);
}