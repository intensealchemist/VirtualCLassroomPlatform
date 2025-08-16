package com.example.demo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PostRequest {
    
    @NotBlank(message = "Post content is required")
    @Size(max = 2000, message = "Post content cannot exceed 2000 characters")
    private String content;
    
    private Long parentPostId;
    
    public PostRequest() {}
    
    public PostRequest(String content) {
        this.content = content;
    }
    
    public PostRequest(String content, Long parentPostId) {
        this.content = content;
        this.parentPostId = parentPostId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Long getParentPostId() {
        return parentPostId;
    }
    
    public void setParentPostId(Long parentPostId) {
        this.parentPostId = parentPostId;
    }
}
