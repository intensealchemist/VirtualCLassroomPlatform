package com.virtualclassroom.service;

import com.virtualclassroom.dto.ChatMessage;
import com.virtualclassroom.dto.NotificationMessage;

/**
 * Service interface for WebSocket operations
 */
public interface WebSocketService {
    
    /**
     * Save a chat message
     * @param chatMessage The chat message
     * @param courseId The course ID
     */
    void saveMessage(ChatMessage chatMessage, String courseId);
    
    /**
     * Send a private notification to a user
     * @param username The username
     * @param message The notification message
     */
    void sendPrivateNotification(String username, NotificationMessage message);
    
    /**
     * Send a notification to all users in a course
     * @param courseId The course ID
     * @param message The notification message
     */
    void sendCourseNotification(String courseId, NotificationMessage message);
}