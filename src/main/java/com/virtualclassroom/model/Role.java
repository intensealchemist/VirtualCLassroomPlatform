package com.virtualclassroom.model;

/**
 * User roles in the Virtual Classroom Platform
 */
public enum Role {
    STUDENT("Student"),
    INSTRUCTOR("Instructor"), 
    ADMIN("Administrator");
    
    private final String displayName;
    
    Role(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean hasPermission(String permission) {
        return switch (this) {
            case ADMIN -> true; // Admin has all permissions
            case INSTRUCTOR -> permission.startsWith("COURSE_") || 
                             permission.startsWith("ASSIGNMENT_") ||
                             permission.startsWith("GRADE_") ||
                             permission.equals("VIEW_ANALYTICS");
            case STUDENT -> permission.equals("VIEW_COURSE") ||
                          permission.equals("SUBMIT_ASSIGNMENT") ||
                          permission.equals("VIEW_GRADES");
        };
    }
}
