package com.virtualclassroom.model;

/**
 * Types of lessons in the Virtual Classroom Platform
 */
public enum LessonType {
    TEXT("Text Content"),
    VIDEO("Video"),
    DOCUMENT("Document"),
    INTERACTIVE("Interactive"),
    QUIZ("Quiz"),
    LIVE_SESSION("Live Session");
    
    private final String displayName;
    
    LessonType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
