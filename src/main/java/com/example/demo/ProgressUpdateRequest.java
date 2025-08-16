package com.example.demo;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class ProgressUpdateRequest {
    
    @NotNull(message = "Completion percentage is required")
    @DecimalMin(value = "0.0", message = "Completion percentage must be at least 0")
    @DecimalMax(value = "100.0", message = "Completion percentage cannot exceed 100")
    private Double completionPercentage;
    
    public ProgressUpdateRequest() {}
    
    public ProgressUpdateRequest(Double completionPercentage) {
        this.completionPercentage = completionPercentage;
    }
    
    public Double getCompletionPercentage() {
        return completionPercentage;
    }
    
    public void setCompletionPercentage(Double completionPercentage) {
        this.completionPercentage = completionPercentage;
    }
}
