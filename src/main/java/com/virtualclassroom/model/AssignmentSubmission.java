package com.virtualclassroom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "assignment_submissions")
public class AssignmentSubmission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status = SubmissionStatus.DRAFT;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal grade;
    
    @Column(name = "max_grade")
    private BigDecimal maxGrade;
    
    @Column(columnDefinition = "TEXT")
    private String feedback;
    
    @Column(name = "attempt_number")
    private Integer attemptNumber = 1;
    
    @Column(name = "time_spent")
    private Long timeSpent; // in minutes
    
    @Column(name = "is_late")
    private Boolean isLate = false;
    
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
    
    @Column(name = "graded_at")
    private LocalDateTime gradedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by")
    private User gradedBy;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ElementCollection
    @CollectionTable(name = "submission_attachments", joinColumns = @JoinColumn(name = "submission_id"))
    private Set<SubmissionAttachment> attachments = new HashSet<>();
    
    // Constructors
    public AssignmentSubmission() {}
    
    public AssignmentSubmission(Assignment assignment, User student) {
        this.assignment = assignment;
        this.student = student;
        this.maxGrade = assignment.getMaxPoints();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper methods
    public void submit() {
        this.status = SubmissionStatus.SUBMITTED;
        this.submittedAt = LocalDateTime.now();
        
        // Check if submission is late
        if (assignment.getDueDate() != null && submittedAt.isAfter(assignment.getDueDate())) {
            this.isLate = true;
        }
    }
    
    public void grade(BigDecimal grade, String feedback, User gradedBy) {
        this.grade = grade;
        this.feedback = feedback;
        this.gradedBy = gradedBy;
        this.gradedAt = LocalDateTime.now();
        this.status = SubmissionStatus.GRADED;
    }
    
    public boolean isSubmitted() {
        return status == SubmissionStatus.SUBMITTED || status == SubmissionStatus.GRADED;
    }
    
    public boolean isGraded() {
        return status == SubmissionStatus.GRADED && grade != null;
    }
    
    public double getGradePercentage() {
        if (grade == null || maxGrade == null || maxGrade.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return grade.divide(maxGrade, 4, BigDecimal.ROUND_HALF_UP)
                   .multiply(BigDecimal.valueOf(100))
                   .doubleValue();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Assignment getAssignment() { return assignment; }
    public void setAssignment(Assignment assignment) { this.assignment = assignment; }
    
    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public SubmissionStatus getStatus() { return status; }
    public void setStatus(SubmissionStatus status) { this.status = status; }
    
    public BigDecimal getGrade() { return grade; }
    public void setGrade(BigDecimal grade) { this.grade = grade; }
    
    public BigDecimal getMaxGrade() { return maxGrade; }
    public void setMaxGrade(BigDecimal maxGrade) { this.maxGrade = maxGrade; }
    
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    
    public Integer getAttemptNumber() { return attemptNumber; }
    public void setAttemptNumber(Integer attemptNumber) { this.attemptNumber = attemptNumber; }
    
    public Long getTimeSpent() { return timeSpent; }
    public void setTimeSpent(Long timeSpent) { this.timeSpent = timeSpent; }
    
    public Boolean getIsLate() { return isLate; }
    public void setIsLate(Boolean isLate) { this.isLate = isLate; }
    
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    
    public LocalDateTime getGradedAt() { return gradedAt; }
    public void setGradedAt(LocalDateTime gradedAt) { this.gradedAt = gradedAt; }
    
    public User getGradedBy() { return gradedBy; }
    public void setGradedBy(User gradedBy) { this.gradedBy = gradedBy; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Set<SubmissionAttachment> getAttachments() { return attachments; }
    public void setAttachments(Set<SubmissionAttachment> attachments) { this.attachments = attachments; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssignmentSubmission)) return false;
        AssignmentSubmission that = (AssignmentSubmission) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "AssignmentSubmission{" +
                "id=" + id +
                ", assignment=" + (assignment != null ? assignment.getTitle() : null) +
                ", student=" + (student != null ? student.getUsername() : null) +
                ", status=" + status +
                ", grade=" + grade +
                ", isLate=" + isLate +
                '}';
    }
}
