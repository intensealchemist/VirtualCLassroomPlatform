package com.example.demo;

public enum AssignmentType {
    ESSAY("Essay"),
    MULTIPLE_CHOICE("Multiple Choice"),
    TRUE_FALSE("True/False"),
    SHORT_ANSWER("Short Answer"),
    PROJECT("Project"),
    PRESENTATION("Presentation"),
    QUIZ("Quiz");
    
    private final String displayName;
    
    AssignmentType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
