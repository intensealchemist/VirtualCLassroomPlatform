package com.virtualclassroom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "lesson_progress",
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "lesson_id"}))
public class LessonProgress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;
    
    @Column(name = "progress_percentage")
    @Min(0) @Max(100)
    private Double progressPercentage = 0.0;
    
    @Column(name = "is_completed")
    private Boolean isCompleted = false;
    
    @Column(name = "time_spent")
    private Long timeSpent = 0L; // in seconds
    
    @Column(name = "last_position")
    private Long lastPosition = 0L; // for video/audio content
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "last_accessed")
    private LocalDateTime lastAccessed;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public LessonProgress() {}
    
    public LessonProgress(User student, Lesson lesson) {
        this.student = student;
        this.lesson = lesson;
        this.startedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        if (startedAt == null) {
            startedAt = LocalDateTime.now();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        lastAccessed = LocalDateTime.now();
    }
    
    // Helper methods
    public void updateProgress(double percentage) {
        this.progressPercentage = Math.min(100.0, Math.max(0.0, percentage));
        
        if (this.progressPercentage >= 100.0 && !isCompleted) {
            this.isCompleted = true;
            this.completedAt = LocalDateTime.now();
        }
    }
    
    public void markCompleted() {
        this.isCompleted = true;
        this.progressPercentage = 100.0;
        this.completedAt = LocalDateTime.now();
    }
    
    public void addTimeSpent(long seconds) {
        this.timeSpent += seconds;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    
    public Lesson getLesson() { return lesson; }
    public void setLesson(Lesson lesson) { this.lesson = lesson; }
    
    public Double getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Double progressPercentage) { this.progressPercentage = progressPercentage; }
    
    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean isCompleted) { this.isCompleted = isCompleted; }
    
    // Additional methods for compatibility
    public boolean isCompleted() { return isCompleted != null && isCompleted; }
    public void setCompleted(boolean completed) { this.isCompleted = completed; }
    
    public Long getTimeSpent() { return timeSpent; }
    public void setTimeSpent(Long timeSpent) { this.timeSpent = timeSpent; }
    
    // Additional methods for compatibility
    public Long getTimeSpentSeconds() { return timeSpent; }
    public void setTimeSpentSeconds(Long timeSpent) { this.timeSpent = timeSpent; }
    
    public Long getLastPosition() { return lastPosition; }
    public void setLastPosition(Long lastPosition) { this.lastPosition = lastPosition; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public LocalDateTime getLastAccessed() { return lastAccessed; }
    public void setLastAccessed(LocalDateTime lastAccessed) { this.lastAccessed = lastAccessed; }
    
    // Additional methods for compatibility
    public LocalDateTime getLastAccessedAt() { return lastAccessed; }
    public void setLastAccessedAt(LocalDateTime lastAccessed) { this.lastAccessed = lastAccessed; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LessonProgress)) return false;
        LessonProgress that = (LessonProgress) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "LessonProgress{" +
                "id=" + id +
                ", student=" + (student != null ? student.getUsername() : null) +
                ", lesson=" + (lesson != null ? lesson.getTitle() : null) +
                ", progressPercentage=" + progressPercentage +
                ", isCompleted=" + isCompleted +
                '}';
    }
}
