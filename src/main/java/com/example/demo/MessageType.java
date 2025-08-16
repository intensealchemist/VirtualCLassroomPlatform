package com.example.demo;

public enum MessageType {
    TEXT("Text"),
    IMAGE("Image"),
    FILE("File"),
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
