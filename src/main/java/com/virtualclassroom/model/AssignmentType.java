package com.virtualclassroom.model;

/**
 * Types of assignments in the Virtual Classroom Platform
 */
public enum AssignmentType {
    ESSAY("Essay"),
    MULTIPLE_CHOICE("Multiple Choice"),
    TRUE_FALSE("True/False"),
    SHORT_ANSWER("Short Answer"),
    FILE_UPLOAD("File Upload"),
    CODE_SUBMISSION("Code Submission"),
    PRESENTATION("Presentation"),
    PROJECT("Project"),
    QUIZ("Quiz"),
    DISCUSSION("Discussion");
    
    private final String displayName;
    
    AssignmentType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
