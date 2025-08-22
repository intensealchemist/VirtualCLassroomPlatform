package com.virtualclassroom.controller;

import com.virtualclassroom.dto.ChatMessage;
import com.virtualclassroom.dto.NotificationMessage;
import com.virtualclassroom.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * Controller for WebSocket communication
 */
@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private WebSocketService webSocketService;

    /**
     * Handle chat messages in a course room
     * @param courseId The course ID
     * @param chatMessage The chat message
     * @param principal The authenticated user
     * @return The chat message
     */
    @MessageMapping("/chat/{courseId}")
    @SendTo("/topic/chat/{courseId}")
    public ChatMessage sendMessage(
            @DestinationVariable String courseId,
            @Payload ChatMessage chatMessage,
            Principal principal) {
        
        chatMessage.setSender(principal.getName());
        chatMessage.setTimestamp(System.currentTimeMillis());
        
        // Save the message to the database
        webSocketService.saveMessage(chatMessage, courseId);
        
        return chatMessage;
    }

    /**
     * Handle user join events in a course room
     * @param courseId The course ID
     * @param chatMessage The chat message
     * @param headerAccessor The message headers
     * @return The chat message
     */
    @MessageMapping("/chat/{courseId}/join")
    @SendTo("/topic/chat/{courseId}")
    public ChatMessage addUser(
            @DestinationVariable String courseId,
            @Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor,
            Principal principal) {
        
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", principal.getName());
        headerAccessor.getSessionAttributes().put("courseId", courseId);
        
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setSender(principal.getName());
        chatMessage.setTimestamp(System.currentTimeMillis());
        
        return chatMessage;
    }

    /**
     * Send a notification to a specific user
     * @param username The username
     * @param message The notification message
     */
    public void sendPrivateNotification(String username, NotificationMessage message) {
        webSocketService.sendPrivateNotification(username, message);
    }

    /**
     * Send a notification to all users in a course
     * @param courseId The course ID
     * @param message The notification message
     */
    public void sendCourseNotification(String courseId, NotificationMessage message) {
        webSocketService.sendCourseNotification(courseId, message);
    }

    /**
     * Send a notification to all users
     * @param message The notification message
     */
    public void sendGlobalNotification(NotificationMessage message) {
        messagingTemplate.convertAndSend(
                "/topic/notifications",
                message
        );
    }
}