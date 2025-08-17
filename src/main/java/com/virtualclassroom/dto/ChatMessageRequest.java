package com.virtualclassroom.dto;

import jakarta.validation.constraints.NotBlank;

public class ChatMessageRequest {
    
    @NotBlank(message = "Message content is required")
    private String content;
    
    private String type = "TEXT";
    
    private String attachmentUrl;
    
    private String attachmentName;
    
    private Long replyToId;
    
    // Constructors
    public ChatMessageRequest() {}
    
    public ChatMessageRequest(String content, String type) {
        this.content = content;
        this.type = type;
    }
    
    // Getters and Setters
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
    
    public String getAttachmentName() { return attachmentName; }
    public void setAttachmentName(String attachmentName) { this.attachmentName = attachmentName; }
    
    public Long getReplyToId() { return replyToId; }
    public void setReplyToId(Long replyToId) { this.replyToId = replyToId; }
}
