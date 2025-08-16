package com.example.demo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChatMessageRequest {
    
    @NotBlank(message = "Message content is required")
    @Size(max = 1000, message = "Message content cannot exceed 1000 characters")
    private String content;
    
    private String roomId;
    private Long courseId;
    private MessageType messageType = MessageType.TEXT;
    
    public ChatMessageRequest() {}
    
    public ChatMessageRequest(String content, String roomId) {
        this.content = content;
        this.roomId = roomId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getRoomId() {
        return roomId;
    }
    
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    
    public Long getCourseId() {
        return courseId;
    }
    
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
    
    public MessageType getMessageType() {
        return messageType;
    }
    
    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}
