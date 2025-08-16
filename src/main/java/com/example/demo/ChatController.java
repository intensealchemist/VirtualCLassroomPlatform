package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class ChatController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessageRequest messageRequest) {
        // Create and save message
        ChatMessage message = new ChatMessage();
        message.setContent(messageRequest.getContent());
        message.setRoomId(messageRequest.getRoomId());
        message.setCourseId(messageRequest.getCourseId());
        message.setMessageType(messageRequest.getMessageType());
        
        // Note: In a real implementation, you'd get the sender from the WebSocket session
        // For now, we'll handle this in the REST endpoint
        
        return chatMessageRepository.save(message);
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                              SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender().getEmail());
        return chatMessage;
    }
}

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*", maxAge = 3600)
class ChatRestController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/send")
    public ResponseEntity<ChatMessage> sendMessage(@RequestBody ChatMessageRequest request,
                                                  @AuthenticationPrincipal CustomUserDetails currentUser) {
        ChatMessage message = new ChatMessage();
        message.setSender(currentUser.getUser());
        message.setContent(request.getContent());
        message.setRoomId(request.getRoomId());
        message.setCourseId(request.getCourseId());
        message.setMessageType(request.getMessageType());

        ChatMessage savedMessage = chatMessageRepository.save(message);

        // Send to WebSocket subscribers
        messagingTemplate.convertAndSend("/topic/room/" + request.getRoomId(), savedMessage);

        return ResponseEntity.ok(savedMessage);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<ChatMessage>> getRoomMessages(@PathVariable String roomId,
                                                           @RequestParam(required = false) String since) {
        List<ChatMessage> messages;
        
        if (since != null) {
            LocalDateTime sinceDateTime = LocalDateTime.parse(since);
            messages = chatMessageRepository.findRecentMessagesByRoom(roomId, sinceDateTime);
        } else {
            messages = chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
        }
        
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ChatMessage>> getCourseMessages(@PathVariable Long courseId) {
        List<ChatMessage> messages = chatMessageRepository.findByCourseIdOrderByCreatedAtDesc(courseId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ChatMessage>> searchMessages(@RequestParam String keyword) {
        List<ChatMessage> messages = chatMessageRepository.findByContentContaining(keyword);
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long messageId,
                                         @AuthenticationPrincipal CustomUserDetails currentUser) {
        Optional<ChatMessage> messageOptional = chatMessageRepository.findById(messageId);
        if (messageOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ChatMessage message = messageOptional.get();
        
        // Only sender or admin can delete
        if (!message.getSender().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.status(403).build();
        }

        chatMessageRepository.delete(message);
        
        // Notify WebSocket subscribers
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId() + "/delete", messageId);
        
        return ResponseEntity.ok().build();
    }
}
