package com.virtualclassroom.model;

/**
 * Types of resources that can be attached to lessons
 */
public enum ResourceType {
    PDF("PDF Document"),
    VIDEO("Video File"),
    AUDIO("Audio File"),
    IMAGE("Image"),
    DOCUMENT("Document"),
    PRESENTATION("Presentation"),
    SPREADSHEET("Spreadsheet"),
    ARCHIVE("Archive/Zip"),
    LINK("External Link"),
    OTHER("Other");
    
    private final String displayName;
    
    ResourceType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
