package com.virtualclassroom.model;

/**
 * Types of chat messages in the Virtual Classroom Platform
 */
public enum MessageType {
    TEXT("Text"),
    IMAGE("Image"),
    FILE("File"),
    VIDEO("Video"),
    AUDIO("Audio"),
    LINK("Link"),
    SYSTEM("System"),
    ANNOUNCEMENT("Announcement");
    
    private final String displayName;
    
    MessageType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
