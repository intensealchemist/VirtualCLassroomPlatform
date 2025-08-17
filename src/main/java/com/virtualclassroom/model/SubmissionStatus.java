package com.virtualclassroom.model;

/**
 * Status of assignment submissions
 */
public enum SubmissionStatus {
    DRAFT("Draft"),
    SUBMITTED("Submitted"),
    GRADED("Graded"),
    RETURNED("Returned"),
    LATE("Late");
    
    private final String displayName;
    
    SubmissionStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
