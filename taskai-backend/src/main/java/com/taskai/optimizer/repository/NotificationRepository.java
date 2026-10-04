package com.taskai.optimizer.repository;

import com.taskai.optimizer.entity.Notification;
import com.taskai.optimizer.enums.NotificationType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @EntityGraph(attributePaths = {"user"})
    List<Notification> findByUserEmailOrderByCreatedAtDesc(String email);

    long countByUserEmailAndIsReadFalse(String email);

    long deleteByIdAndUserEmail(Long id, String email);

    @EntityGraph(attributePaths = {"user"})
    Optional<Notification> findByIdAndUserEmail(Long id, String email);

    @Transactional
    @Modifying
    @Query("""
        update Notification n
        set n.isRead = true
        where n.user.email = :email and n.isRead = false
    """)
    int markAllAsReadByUserEmail(String email);

    boolean existsByUserEmailAndTypeAndTitleAndCreatedAtAfter(
            String email,
            NotificationType type,
            String title,
            LocalDateTime createdAt
    );
}