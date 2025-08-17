package com.virtualclassroom.model;

/**
 * Types of notifications in the Virtual Classroom Platform
 */
public enum NotificationType {
    INFO("Information"),
    SUCCESS("Success"),
    WARNING("Warning"),
    ERROR("Error"),
    ASSIGNMENT("Assignment"),
    COURSE("Course"),
    MESSAGE("Message"),
    GRADE("Grade"),
    ENROLLMENT("Enrollment"),
    REMINDER("Reminder"),
    SYSTEM("System");
    
    private final String displayName;
    
    NotificationType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
