package com.example.demo;

public class DashboardStats {
    
    private long totalCourses;
    private long totalStudents;
    private long totalInstructors;
    private long totalAssignments;
    private long totalLessons;
    private long activeEnrollments;
    private double averageProgress;
    private long totalForums;
    private long totalPosts;
    private long unreadNotifications;
    
    public DashboardStats() {}
    
    public DashboardStats(long totalCourses, long totalStudents, long totalInstructors, 
                         long totalAssignments, long totalLessons, long activeEnrollments,
                         double averageProgress, long totalForums, long totalPosts, long unreadNotifications) {
        this.totalCourses = totalCourses;
        this.totalStudents = totalStudents;
        this.totalInstructors = totalInstructors;
        this.totalAssignments = totalAssignments;
        this.totalLessons = totalLessons;
        this.activeEnrollments = activeEnrollments;
        this.averageProgress = averageProgress;
        this.totalForums = totalForums;
        this.totalPosts = totalPosts;
        this.unreadNotifications = unreadNotifications;
    }
    
    // Getters and Setters
    public long getTotalCourses() {
        return totalCourses;
    }
    
    public void setTotalCourses(long totalCourses) {
        this.totalCourses = totalCourses;
    }
    
    public long getTotalStudents() {
        return totalStudents;
    }
    
    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }
    
    public long getTotalInstructors() {
        return totalInstructors;
    }
    
    public void setTotalInstructors(long totalInstructors) {
        this.totalInstructors = totalInstructors;
    }
    
    public long getTotalAssignments() {
        return totalAssignments;
    }
    
    public void setTotalAssignments(long totalAssignments) {
        this.totalAssignments = totalAssignments;
    }
    
    public long getTotalLessons() {
        return totalLessons;
    }
    
    public void setTotalLessons(long totalLessons) {
        this.totalLessons = totalLessons;
    }
    
    public long getActiveEnrollments() {
        return activeEnrollments;
    }
    
    public void setActiveEnrollments(long activeEnrollments) {
        this.activeEnrollments = activeEnrollments;
    }
    
    public double getAverageProgress() {
        return averageProgress;
    }
    
    public void setAverageProgress(double averageProgress) {
        this.averageProgress = averageProgress;
    }
    
    public long getTotalForums() {
        return totalForums;
    }
    
    public void setTotalForums(long totalForums) {
        this.totalForums = totalForums;
    }
    
    public long getTotalPosts() {
        return totalPosts;
    }
    
    public void setTotalPosts(long totalPosts) {
        this.totalPosts = totalPosts;
    }
    
    public long getUnreadNotifications() {
        return unreadNotifications;
    }
    
    public void setUnreadNotifications(long unreadNotifications) {
        this.unreadNotifications = unreadNotifications;
    }
}
