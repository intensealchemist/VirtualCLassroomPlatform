package com.virtualclassroom.service;

import com.virtualclassroom.model.ChatMessage;
import com.virtualclassroom.model.Course;
import com.virtualclassroom.model.MessageType;
import com.virtualclassroom.model.User;

import java.util.List;

/**
 * Service interface for chat message operations
 */
public interface ChatMessageService {
    
    /**
     * Create a new course message
     * @param content Message content
     * @param sender Message sender
     * @param course Course context
     * @param type Message type
     * @return Created ChatMessage
     */
    ChatMessage createCourseMessage(String content, User sender, Course course, MessageType type);
    
    /**
     * Create a new direct message
     * @param content Message content
     * @param sender Message sender
     * @param recipient Message recipient
     * @param type Message type
     * @return Created ChatMessage
     */
    ChatMessage createDirectMessage(String content, User sender, User recipient, MessageType type);
    
    /**
     * Get messages for a course with pagination
     * @param course Course
     * @param page Page number
     * @param size Page size
     * @return List of ChatMessage
     */
    List<ChatMessage> getCourseMessages(Course course, int page, int size);
    
    /**
     * Get direct messages between two users with pagination
     * @param user1 First user
     * @param user2 Second user
     * @param page Page number
     * @param size Page size
     * @return List of ChatMessage
     */
    List<ChatMessage> getDirectMessages(User user1, User user2, int page, int size);
    
    /**
     * Edit a message
     * @param messageId Message ID
     * @param newContent New message content
     * @param user User performing the edit
     * @return Updated ChatMessage
     */
    ChatMessage editMessage(Long messageId, String newContent, User user);
    
    /**
     * Delete a message
     * @param messageId Message ID
     * @param user User performing the deletion
     */
    void deleteMessage(Long messageId, User user);
    
    /**
     * Get recent conversations for a user
     * @param user User
     * @return List of ChatMessage
     */
    List<ChatMessage> getRecentConversations(User user);
    
    /**
     * Get unread message count for a user
     * @param user User
     * @return Unread message count
     */
    long getUnreadMessageCount(User user);
}