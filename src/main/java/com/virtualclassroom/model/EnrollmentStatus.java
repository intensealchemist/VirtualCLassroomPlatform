package com.virtualclassroom.model;

/**
 * Status of course enrollment
 */
public enum EnrollmentStatus {
    ACTIVE("Active"),
    COMPLETED("Completed"),
    DROPPED("Dropped"),
    SUSPENDED("Suspended"),
    PENDING("Pending");
    
    private final String displayName;
    
    EnrollmentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
