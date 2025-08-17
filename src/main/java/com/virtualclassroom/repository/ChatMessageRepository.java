package com.virtualclassroom.repository;

import com.virtualclassroom.model.ChatMessage;
import com.virtualclassroom.model.Course;
import com.virtualclassroom.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    List<ChatMessage> findByCourseAndIsDeletedFalseOrderByCreatedAtDesc(Course course, Pageable pageable);
    
    @Query("SELECT m FROM ChatMessage m WHERE " +
           "((m.sender = :user1 AND m.recipient = :user2) OR (m.sender = :user2 AND m.recipient = :user1)) " +
           "AND m.isDeleted = false ORDER BY m.createdAt DESC")
    List<ChatMessage> findDirectMessagesBetweenUsers(@Param("user1") User user1, @Param("user2") User user2, Pageable pageable);
    
    @Query("SELECT DISTINCT m FROM ChatMessage m WHERE " +
           "(m.sender = :user OR m.recipient = :user) AND m.isDeleted = false " +
           "ORDER BY m.createdAt DESC")
    List<ChatMessage> findRecentConversations(@Param("user") User user);
    
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.recipient = :user AND m.isDeleted = false")
    long countUnreadMessages(@Param("user") User user);
    
    List<ChatMessage> findBySenderOrderByCreatedAtDesc(User sender);
    
    List<ChatMessage> findByRecipientOrderByCreatedAtDesc(User recipient);
    
    @Query("SELECT m FROM ChatMessage m WHERE m.course = :course AND m.content LIKE %:searchTerm% AND m.isDeleted = false")
    List<ChatMessage> searchCourseMessages(@Param("course") Course course, @Param("searchTerm") String searchTerm);
}
