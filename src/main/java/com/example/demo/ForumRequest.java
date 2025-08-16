package com.example.demo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ForumRequest {
    
    @NotBlank(message = "Forum title is required")
    @Size(max = 200, message = "Forum title cannot exceed 200 characters")
    private String title;
    
    @Size(max = 1000, message = "Forum description cannot exceed 1000 characters")
    private String description;
    
    @NotNull(message = "Course ID is required")
    private Long courseId;
    
    public ForumRequest() {}
    
    public ForumRequest(String title, String description, Long courseId) {
        this.title = title;
        this.description = description;
        this.courseId = courseId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getCourseId() {
        return courseId;
    }
    
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
