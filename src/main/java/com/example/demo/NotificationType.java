package com.example.demo;

public enum NotificationType {
    COURSE_ENROLLMENT("Course Enrollment"),
    ASSIGNMENT_DUE("Assignment Due"),
    ASSIGNMENT_GRADED("Assignment Graded"),
    NEW_LESSON("New Lesson"),
    COURSE_UPDATE("Course Update"),
    SYSTEM_ANNOUNCEMENT("System Announcement"),
    CHAT_MESSAGE("Chat Message"),
    DISCUSSION_REPLY("Discussion Reply");
    
    private final String displayName;
    
    NotificationType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
