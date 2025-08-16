package com.example.demo;

public class EnrollmentStats {
    
    private Long totalEnrollments;
    private Double averageCompletion;
    
    public EnrollmentStats() {}
    
    public EnrollmentStats(Long totalEnrollments, Double averageCompletion) {
        this.totalEnrollments = totalEnrollments;
        this.averageCompletion = averageCompletion != null ? averageCompletion : 0.0;
    }
    
    public Long getTotalEnrollments() {
        return totalEnrollments;
    }
    
    public void setTotalEnrollments(Long totalEnrollments) {
        this.totalEnrollments = totalEnrollments;
    }
    
    public Double getAverageCompletion() {
        return averageCompletion;
    }
    
    public void setAverageCompletion(Double averageCompletion) {
        this.averageCompletion = averageCompletion;
    }
}
