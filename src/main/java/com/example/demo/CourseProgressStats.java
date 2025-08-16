package com.example.demo;

public class CourseProgressStats {
    
    private Long courseId;
    private Double averageCompletion;
    private Long totalEnrollments;
    
    public CourseProgressStats() {}
    
    public CourseProgressStats(Long courseId, Double averageCompletion, Long totalEnrollments) {
        this.courseId = courseId;
        this.averageCompletion = averageCompletion != null ? averageCompletion : 0.0;
        this.totalEnrollments = totalEnrollments;
    }
    
    public Long getCourseId() {
        return courseId;
    }
    
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
    
    public Double getAverageCompletion() {
        return averageCompletion;
    }
    
    public void setAverageCompletion(Double averageCompletion) {
        this.averageCompletion = averageCompletion;
    }
    
    public Long getTotalEnrollments() {
        return totalEnrollments;
    }
    
    public void setTotalEnrollments(Long totalEnrollments) {
        this.totalEnrollments = totalEnrollments;
    }
}
