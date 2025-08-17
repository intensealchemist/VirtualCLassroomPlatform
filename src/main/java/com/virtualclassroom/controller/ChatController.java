package com.virtualclassroom.controller;

import com.virtualclassroom.dto.ChatMessageRequest;
import com.virtualclassroom.model.ChatMessage;
import com.virtualclassroom.model.Course;
import com.virtualclassroom.model.MessageType;
import com.virtualclassroom.model.User;
import com.virtualclassroom.service.ChatService;
import com.virtualclassroom.service.CourseService;
import com.virtualclassroom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class ChatController {
    
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/chat.sendMessage/{courseId}")
    @SendTo("/topic/course/{courseId}")
    public ChatMessage sendMessage(@DestinationVariable Long courseId, 
                                 @Payload ChatMessageRequest messageRequest,
                                 Principal principal) {
        
        User sender = userService.getUserByUsername(principal.getName());
        Course course = courseService.getCourseById(courseId);
        
        // Verify user is enrolled in the course
        if (!courseService.isStudentEnrolled(sender, course) && 
            !course.getInstructor().equals(sender) && 
            !sender.isAdmin()) {
            throw new RuntimeException("Not authorized to send messages in this course");
        }
        
        ChatMessage message = chatService.createCourseMessage(
            messageRequest.getContent(), 
            sender, 
            course, 
            MessageType.valueOf(messageRequest.getType().toUpperCase())
        );
        
        return message;
    }
    
    @MessageMapping("/chat.sendDirectMessage/{recipientId}")
    public void sendDirectMessage(@DestinationVariable Long recipientId,
                                @Payload ChatMessageRequest messageRequest,
                                Principal principal) {
        
        User sender = userService.getUserByUsername(principal.getName());
        User recipient = userService.getUserById(recipientId);
        
        ChatMessage message = chatService.createDirectMessage(
            messageRequest.getContent(),
            sender,
            recipient,
            MessageType.valueOf(messageRequest.getType().toUpperCase())
        );
        
        // Send to recipient's personal queue
        messagingTemplate.convertAndSendToUser(
            recipient.getUsername(),
            "/queue/messages",
            message
        );
    }
    
    @MessageMapping("/chat.addUser/{courseId}")
    @SendTo("/topic/course/{courseId}")
    public ChatMessage addUser(@DestinationVariable Long courseId,
                             SimpMessageHeaderAccessor headerAccessor,
                             Principal principal) {
        
        User user = userService.getUserByUsername(principal.getName());
        Course course = courseService.getCourseById(courseId);
        
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", user.getUsername());
        headerAccessor.getSessionAttributes().put("courseId", courseId);
        
        ChatMessage message = new ChatMessage();
        message.setType(MessageType.SYSTEM);
        message.setContent(user.getFullName() + " joined the chat");
        message.setSender(user);
        message.setCourse(course);
        
        return message;
    }
    
    @MessageMapping("/chat.typing/{courseId}")
    @SendTo("/topic/course/{courseId}/typing")
    public ChatMessage userTyping(@DestinationVariable Long courseId,
                                Principal principal) {
        
        User user = userService.getUserByUsername(principal.getName());
        
        ChatMessage typingMessage = new ChatMessage();
        typingMessage.setType(MessageType.SYSTEM);
        typingMessage.setContent("typing");
        typingMessage.setSender(user);
        
        return typingMessage;
    }
    
    @GetMapping("/api/chat/course/{courseId}/messages")
    @ResponseBody
    public List<ChatMessage> getCourseMessages(@PathVariable Long courseId,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "50") int size,
                                             Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        Course course = courseService.getCourseById(courseId);
        
        // Verify access
        if (!courseService.isStudentEnrolled(user, course) && 
            !course.getInstructor().equals(user) && 
            !user.isAdmin()) {
            throw new RuntimeException("Not authorized to view messages in this course");
        }
        
        return chatService.getCourseMessages(course, page, size);
    }
    
    @GetMapping("/api/chat/direct/{userId}/messages")
    @ResponseBody
    public List<ChatMessage> getDirectMessages(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "50") int size,
                                             Authentication authentication) {
        
        User currentUser = (User) authentication.getPrincipal();
        User otherUser = userService.getUserById(userId);
        
        return chatService.getDirectMessages(currentUser, otherUser, page, size);
    }
    
    @PostMapping("/api/chat/message/{messageId}/edit")
    @ResponseBody
    public ChatMessage editMessage(@PathVariable Long messageId,
                                 @RequestBody ChatMessageRequest request,
                                 Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        return chatService.editMessage(messageId, request.getContent(), user);
    }
    
    @DeleteMapping("/api/chat/message/{messageId}")
    @ResponseBody
    public void deleteMessage(@PathVariable Long messageId,
                            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        chatService.deleteMessage(messageId, user);
    }
    
    @GetMapping("/api/chat/conversations")
    @ResponseBody
    public List<ChatMessage> getRecentConversations(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return chatService.getRecentConversations(user);
    }
}
