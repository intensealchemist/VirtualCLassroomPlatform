package com.example.demo;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class LessonProgressRequest {
    
    @NotNull(message = "Completion percentage is required")
    @DecimalMin(value = "0.0", message = "Completion percentage must be at least 0")
    @DecimalMax(value = "100.0", message = "Completion percentage cannot exceed 100")
    private Double completionPercentage;
    
    @Min(value = 0, message = "Time spent cannot be negative")
    private Integer timeSpentMinutes = 0;
    
    public LessonProgressRequest() {}
    
    public LessonProgressRequest(Double completionPercentage, Integer timeSpentMinutes) {
        this.completionPercentage = completionPercentage;
        this.timeSpentMinutes = timeSpentMinutes;
    }
    
    public Double getCompletionPercentage() {
        return completionPercentage;
    }
    
    public void setCompletionPercentage(Double completionPercentage) {
        this.completionPercentage = completionPercentage;
    }
    
    public Integer getTimeSpentMinutes() {
        return timeSpentMinutes;
    }
    
    public void setTimeSpentMinutes(Integer timeSpentMinutes) {
        this.timeSpentMinutes = timeSpentMinutes;
    }
}
