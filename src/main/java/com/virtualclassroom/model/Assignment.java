package com.virtualclassroom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "assignments")
public class Assignment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Assignment title is required")
    @Size(max = 200, message = "Assignment title must not exceed 200 characters")
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String instructions;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentType type = AssignmentType.ESSAY;
    
    @Column(name = "max_points")
    private BigDecimal maxPoints;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @Column(name = "available_from")
    private LocalDateTime availableFrom;
    
    @Column(name = "available_until")
    private LocalDateTime availableUntil;
    
    @Column(name = "allow_late_submission")
    private Boolean allowLateSubmission = false;
    
    @Column(name = "late_penalty_percentage")
    private BigDecimal latePenaltyPercentage;
    
    @Column(name = "max_attempts")
    private Integer maxAttempts = 1;
    
    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes;
    
    @Column(name = "is_published")
    private Boolean isPublished = false;
    
    @Column(name = "auto_grade")
    private Boolean autoGrade = false;
    
    @Column(name = "show_correct_answers")
    private Boolean showCorrectAnswers = false;
    
    @Column(name = "randomize_questions")
    private Boolean randomizeQuestions = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @NotNull(message = "Course is required")
    private Course course;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Creator is required")
    private User user;
    
    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AssignmentSubmission> submissions = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "assignment_attachments", joinColumns = @JoinColumn(name = "assignment_id"))
    private Set<AssignmentAttachment> attachments = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "assignment_rubric_criteria", joinColumns = @JoinColumn(name = "assignment_id"))
    private Set<RubricCriterion> rubricCriteria = new HashSet<>();
    
    // Constructors
    public Assignment() {}
    
    public Assignment(String title, String description, Course course, User user) {
        this.title = title;
        this.description = description;
        this.course = course;
        this.user = user;
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
    public boolean isAvailable() {
        LocalDateTime now = LocalDateTime.now();
        return isPublished && 
               (availableFrom == null || now.isAfter(availableFrom)) &&
               (availableUntil == null || now.isBefore(availableUntil));
    }
    
    public boolean isPastDue() {
        return dueDate != null && LocalDateTime.now().isAfter(dueDate);
    }
    
    public boolean canSubmit(User student) {
        if (!isAvailable()) return false;
        if (isPastDue() && !allowLateSubmission) return false;
        
        long submissionCount = submissions.stream()
                .filter(sub -> sub.getStudent().equals(student))
                .count();
        
        return maxAttempts == null || submissionCount < maxAttempts;
    }
    
    public int getSubmissionCount() {
        return submissions.size();
    }
    
    public double getAverageGrade() {
        return submissions.stream()
                .filter(sub -> sub.getGrade() != null)
                .mapToDouble(sub -> sub.getGrade().doubleValue())
                .average()
                .orElse(0.0);
    }
    
    public AssignmentSubmission getSubmissionByStudent(User student) {
        return submissions.stream()
                .filter(sub -> sub.getStudent().equals(student))
                .findFirst()
                .orElse(null);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    
    public AssignmentType getType() { return type; }
    public void setType(AssignmentType type) { this.type = type; }
    
    public BigDecimal getMaxPoints() { return maxPoints; }
    public void setMaxPoints(BigDecimal maxPoints) { this.maxPoints = maxPoints; }
    
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    
    public LocalDateTime getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(LocalDateTime availableFrom) { this.availableFrom = availableFrom; }
    
    public LocalDateTime getAvailableUntil() { return availableUntil; }
    public void setAvailableUntil(LocalDateTime availableUntil) { this.availableUntil = availableUntil; }
    
    public Boolean getAllowLateSubmission() { return allowLateSubmission; }
    public void setAllowLateSubmission(Boolean allowLateSubmission) { this.allowLateSubmission = allowLateSubmission; }
    
    public BigDecimal getLatePenaltyPercentage() { return latePenaltyPercentage; }
    public void setLatePenaltyPercentage(BigDecimal latePenaltyPercentage) { this.latePenaltyPercentage = latePenaltyPercentage; }
    
    public Integer getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(Integer maxAttempts) { this.maxAttempts = maxAttempts; }
    
    public Integer getTimeLimitMinutes() { return timeLimitMinutes; }
    public void setTimeLimitMinutes(Integer timeLimitMinutes) { this.timeLimitMinutes = timeLimitMinutes; }
    
    public Boolean getIsPublished() { return isPublished; }
    public void setIsPublished(Boolean isPublished) { this.isPublished = isPublished; }
    
    public Boolean getAutoGrade() { return autoGrade; }
    public void setAutoGrade(Boolean autoGrade) { this.autoGrade = autoGrade; }
    
    public Boolean getShowCorrectAnswers() { return showCorrectAnswers; }
    public void setShowCorrectAnswers(Boolean showCorrectAnswers) { this.showCorrectAnswers = showCorrectAnswers; }
    
    public Boolean getRandomizeQuestions() { return randomizeQuestions; }
    public void setRandomizeQuestions(Boolean randomizeQuestions) { this.randomizeQuestions = randomizeQuestions; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Set<AssignmentSubmission> getSubmissions() { return submissions; }
    public void setSubmissions(Set<AssignmentSubmission> submissions) { this.submissions = submissions; }
    
    public Set<AssignmentAttachment> getAttachments() { return attachments; }
    public void setAttachments(Set<AssignmentAttachment> attachments) { this.attachments = attachments; }
    
    public Set<RubricCriterion> getRubricCriteria() { return rubricCriteria; }
    public void setRubricCriteria(Set<RubricCriterion> rubricCriteria) { this.rubricCriteria = rubricCriteria; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Assignment)) return false;
        Assignment that = (Assignment) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Assignment{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", maxPoints=" + maxPoints +
                ", dueDate=" + dueDate +
                ", isPublished=" + isPublished +
                '}';
    }
}
