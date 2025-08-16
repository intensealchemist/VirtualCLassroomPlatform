package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    Long countUnreadByUser(@Param("userId") Long userId);
    
    List<Notification> findByNotificationTypeAndCreatedAtAfter(NotificationType type, LocalDateTime since);
    
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.createdAt >= :since ORDER BY n.createdAt DESC")
    List<Notification> findRecentByUser(@Param("userId") Long userId, @Param("since") LocalDateTime since);
    
    void deleteByUserIdAndCreatedAtBefore(Long userId, LocalDateTime before);
}
