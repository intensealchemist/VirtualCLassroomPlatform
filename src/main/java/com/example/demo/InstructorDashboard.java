package com.example.demo;

import java.util.List;

public class InstructorDashboard {
    
    private long totalCourses;
    private long totalStudents;
    private long totalAssignments;
    private long totalLessons;
    private double averageStudentProgress;
    private long activeForums;
    private long unreadNotifications;
    private List<Course> recentCourses;
    private List<Assignment> recentAssignments;
    private List<CourseEnrollment> recentEnrollments;
    private List<Notification> recentNotifications;
    
    public InstructorDashboard() {}
    
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
    
    public double getAverageStudentProgress() {
        return averageStudentProgress;
    }
    
    public void setAverageStudentProgress(double averageStudentProgress) {
        this.averageStudentProgress = averageStudentProgress;
    }
    
    public long getActiveForums() {
        return activeForums;
    }
    
    public void setActiveForums(long activeForums) {
        this.activeForums = activeForums;
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
    
    public List<Assignment> getRecentAssignments() {
        return recentAssignments;
    }
    
    public void setRecentAssignments(List<Assignment> recentAssignments) {
        this.recentAssignments = recentAssignments;
    }
    
    public List<CourseEnrollment> getRecentEnrollments() {
        return recentEnrollments;
    }
    
    public void setRecentEnrollments(List<CourseEnrollment> recentEnrollments) {
        this.recentEnrollments = recentEnrollments;
    }
    
    public List<Notification> getRecentNotifications() {
        return recentNotifications;
    }
    
    public void setRecentNotifications(List<Notification> recentNotifications) {
        this.recentNotifications = recentNotifications;
    }
}
