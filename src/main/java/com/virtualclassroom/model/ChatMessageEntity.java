package com.virtualclassroom.model;

import java.time.LocalDateTime;

/**
 * Entity for chat messages
 */
// NOTE: This class is intentionally not a JPA entity to avoid duplicate mapping with `ChatMessage`.
// It remains as a plain POJO. The active JPA entity for the `chat_messages` table is `ChatMessage`.
public class ChatMessageEntity {

    private Long id;

    private String content;

    private User sender;

    private Course course;

    private User recipient;

    private MessageType type;

    private LocalDateTime createdAt;

    private boolean isDeleted = false;

    public ChatMessageEntity() {
        this.createdAt = LocalDateTime.now();
        this.type = MessageType.TEXT;
    }

    public ChatMessageEntity(String content, User sender, Course course) {
        this();
        this.content = content;
        this.sender = sender;
        this.course = course;
    }

    public ChatMessageEntity(String content, User sender, User recipient) {
        this();
        this.content = content;
        this.sender = sender;
        this.recipient = recipient;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}