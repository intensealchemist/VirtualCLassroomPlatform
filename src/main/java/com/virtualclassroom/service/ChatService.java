package com.virtualclassroom.service;

import com.virtualclassroom.model.ChatMessage;
import com.virtualclassroom.model.Course;
import com.virtualclassroom.model.MessageType;
import com.virtualclassroom.model.User;
import com.virtualclassroom.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ChatService implements ChatMessageService {
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    public ChatMessage createCourseMessage(String content, User sender, Course course, MessageType type) {
        ChatMessage message = new ChatMessage(content, sender, course);
        message.setType(type);
        return chatMessageRepository.save(message);
    }
    
    public ChatMessage createDirectMessage(String content, User sender, User recipient, MessageType type) {
        ChatMessage message = new ChatMessage(content, sender, recipient);
        message.setType(type);
        return chatMessageRepository.save(message);
    }
    
    public List<ChatMessage> getCourseMessages(Course course, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return chatMessageRepository.findByCourseAndIsDeletedFalseOrderByCreatedAtDesc(course, pageable);
    }
    
    public List<ChatMessage> getDirectMessages(User user1, User user2, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return chatMessageRepository.findDirectMessagesBetweenUsers(user1, user2, pageable);
    }
    
    public ChatMessage editMessage(Long messageId, String newContent, User user) {
        ChatMessage message = chatMessageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("Message not found"));
        
        if (!message.getSender().equals(user)) {
            throw new RuntimeException("You can only edit your own messages");
        }
        
        if (message.getIsDeleted()) {
            throw new RuntimeException("Cannot edit deleted message");
        }
        
        message.setContent(newContent);
        message.markAsEdited();
        
        return chatMessageRepository.save(message);
    }
    
    public void deleteMessage(Long messageId, User user) {
        ChatMessage message = chatMessageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("Message not found"));
        
        if (!message.getSender().equals(user) && !user.isAdmin()) {
            throw new RuntimeException("You can only delete your own messages");
        }
        
        message.markAsDeleted();
        chatMessageRepository.save(message);
    }
    
    public List<ChatMessage> getRecentConversations(User user) {
        return chatMessageRepository.findRecentConversations(user);
    }
    
    public long getUnreadMessageCount(User user) {
        return chatMessageRepository.countUnreadMessages(user);
    }
}
