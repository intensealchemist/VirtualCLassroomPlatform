package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(String roomId);
    
    List<ChatMessage> findByCourseIdOrderByCreatedAtDesc(Long courseId);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.roomId = :roomId AND cm.createdAt >= :since ORDER BY cm.createdAt ASC")
    List<ChatMessage> findRecentMessagesByRoom(@Param("roomId") String roomId, @Param("since") LocalDateTime since);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.sender.id = :senderId ORDER BY cm.createdAt DESC")
    List<ChatMessage> findBySenderId(@Param("senderId") Long senderId);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.content LIKE %:keyword% ORDER BY cm.createdAt DESC")
    List<ChatMessage> findByContentContaining(@Param("keyword") String keyword);
    
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.roomId = :roomId")
    Long countMessagesByRoom(@Param("roomId") String roomId);
}
