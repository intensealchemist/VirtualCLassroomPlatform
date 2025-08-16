package com.example.demo;

import java.util.List;

public class StudentDashboard {
    
    private long enrolledCourses;
    private long completedLessons;
    private long pendingAssignments;
    private double averageProgress;
    private long unreadNotifications;
    private List<Course> recentCourses;
    private List<Assignment> upcomingAssignments;
    private List<Notification> recentNotifications;
    
    public StudentDashboard() {}
    
    // Getters and Setters
    public long getEnrolledCourses() {
        return enrolledCourses;
    }
    
    public void setEnrolledCourses(long enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }
    
    public long getCompletedLessons() {
        return completedLessons;
    }
    
    public void setCompletedLessons(long completedLessons) {
        this.completedLessons = completedLessons;
    }
    
    public long getPendingAssignments() {
        return pendingAssignments;
    }
    
    public void setPendingAssignments(long pendingAssignments) {
        this.pendingAssignments = pendingAssignments;
    }
    
    public double getAverageProgress() {
        return averageProgress;
    }
    
    public void setAverageProgress(double averageProgress) {
        this.averageProgress = averageProgress;
    }
    
    public long getUnreadNotifications() {
        return unreadNotifications;
    }
    
    public void setUnreadNotifications(long unreadNotifications) {
        this.unreadNotifications = unreadNotifications;
    }
    
    public List<Course> getRecentCourses() {
        return recentCourses;
    }
    
    public void setRecentCourses(List<Course> recentCourses) {
        this.recentCourses = recentCourses;
    }
    
    public List<Assignment> getUpcomingAssignments() {
        return upcomingAssignments;
    }
    
    public void setUpcomingAssignments(List<Assignment> upcomingAssignments) {
        this.upcomingAssignments = upcomingAssignments;
    }
    
    public List<Notification> getRecentNotifications() {
        return recentNotifications;
    }
    
    public void setRecentNotifications(List<Notification> recentNotifications) {
        this.recentNotifications = recentNotifications;
    }
}
