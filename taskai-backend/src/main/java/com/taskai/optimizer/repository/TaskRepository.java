package com.taskai.optimizer.repository;

import com.taskai.optimizer.entity.Task;
import com.taskai.optimizer.enums.TaskPriority;
import com.taskai.optimizer.enums.TaskStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // ===============================
    // GLOBAL STATISTICS
    // ===============================
    long countByStatus(TaskStatus status);

    long countByPriority(TaskPriority priority);

    long countByDueDateBeforeAndStatusNot(LocalDateTime dateTime, TaskStatus status);


    // ===============================
    // USER STATISTICS
    // ===============================
    long countByUserEmail(String email);

    long countByUserEmailAndStatus(String email, TaskStatus status);

    long countByUserEmailAndPriority(String email, TaskPriority priority);

    long countByUserEmailAndDueDateBeforeAndStatusNot(
            String email,
            LocalDateTime dateTime,
            TaskStatus status
    );


    // ===============================
    // FETCH TASKS (IMPORTANT FIX)
    // ===============================

    /**
     * Load tasks with associated user (avoid LazyInitializationException)
     */
    @EntityGraph(attributePaths = {"user"})
    List<Task> findByUserEmail(String email);

    /**
     * Load active tasks (not DONE)
     */
    @EntityGraph(attributePaths = {"user"})
    List<Task> findByUserEmailAndStatusNot(String email, TaskStatus status);


    // ===============================
    // OVERRIDES (FORCE USER FETCH)
    // ===============================

    @Override
    @EntityGraph(attributePaths = {"user"})
    List<Task> findAll();

    @Override
    @EntityGraph(attributePaths = {"user"})
    Optional<Task> findById(Long id);
}