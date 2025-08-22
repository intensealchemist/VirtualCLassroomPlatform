package com.virtualclassroom.service.impl;

import com.virtualclassroom.dto.ChatMessage;
import com.virtualclassroom.dto.NotificationMessage;
import com.virtualclassroom.model.Course;
import com.virtualclassroom.model.MessageType;
import com.virtualclassroom.model.User;
import com.virtualclassroom.repository.CourseRepository;
import com.virtualclassroom.repository.UserRepository;
import com.virtualclassroom.service.ChatMessageService;
import com.virtualclassroom.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Implementation of WebSocketService interface
 */
@Service
public class WebSocketServiceImpl implements WebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private ChatMessageService chatMessageService;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public void saveMessage(ChatMessage chatMessage, String courseId) {
        try {
            Long courseIdLong = Long.parseLong(courseId);
            Course course = courseRepository.findById(courseIdLong)
                .orElseThrow(() -> new RuntimeException("Course not found"));
            
            User sender = userRepository.findByUsername(chatMessage.getSender())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            MessageType messageType = MessageType.valueOf(chatMessage.getType().name());
            
            chatMessageService.createCourseMessage(
                chatMessage.getContent(),
                sender,
                course,
                messageType
            );
        } catch (Exception e) {
            // Log the error but don't propagate it to avoid WebSocket connection issues
            System.err.println("Error saving message: " + e.getMessage());
        }
    }
    
    @Override
    public void sendPrivateNotification(String username, NotificationMessage message) {
        messagingTemplate.convertAndSendToUser(
                username,
                "/queue/notifications",
                message
        );
    }
    
    @Override
    public void sendCourseNotification(String courseId, NotificationMessage message) {
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + courseId,
                message
        );
    }
}