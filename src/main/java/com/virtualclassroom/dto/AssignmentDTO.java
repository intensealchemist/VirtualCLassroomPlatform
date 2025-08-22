package com.virtualclassroom.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Assignment
 */
public class AssignmentDTO {

    private Long id;
    private String title;
    private String description;
    private String instructions;
    private Long courseId;
    private String courseName;
    private LocalDateTime dueDate;
    private Integer maxPoints;
    private String attachmentUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isPublished;
    private int submissionCount;
    private int gradedCount;

    /**
     * Default constructor
     */
    public AssignmentDTO() {
    }

    /**
     * Get the assignment ID
     * @return The assignment ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the assignment ID
     * @param id The assignment ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the assignment title
     * @return The assignment title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the assignment title
     * @param title The assignment title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the assignment description
     * @return The assignment description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the assignment description
     * @param description The assignment description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the assignment instructions
     * @return The assignment instructions
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * Set the assignment instructions
     * @param instructions The assignment instructions
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    /**
     * Get the course ID
     * @return The course ID
     */
    public Long getCourseId() {
        return courseId;
    }

    /**
     * Set the course ID
     * @param courseId The course ID
     */
    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    /**
     * Get the course name
     * @return The course name
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * Set the course name
     * @param courseName The course name
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /**
     * Get the due date
     * @return The due date
     */
    public LocalDateTime getDueDate() {
        return dueDate;
    }

    /**
     * Set the due date
     * @param dueDate The due date
     */
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Get the maximum points
     * @return The maximum points
     */
    public Integer getMaxPoints() {
        return maxPoints;
    }

    /**
     * Set the maximum points
     * @param maxPoints The maximum points
     */
    public void setMaxPoints(Integer maxPoints) {
        this.maxPoints = maxPoints;
    }

    /**
     * Get the attachment URL
     * @return The attachment URL
     */
    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    /**
     * Set the attachment URL
     * @param attachmentUrl The attachment URL
     */
    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    /**
     * Get the creation timestamp
     * @return The creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Set the creation timestamp
     * @param createdAt The creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Get the update timestamp
     * @return The update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Set the update timestamp
     * @param updatedAt The update timestamp
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Check if the assignment is published
     * @return True if published, false otherwise
     */
    public boolean isPublished() {
        return isPublished;
    }

    /**
     * Set the published status
     * @param published The published status
     */
    public void setPublished(boolean published) {
        isPublished = published;
    }

    /**
     * Get the submission count
     * @return The submission count
     */
    public int getSubmissionCount() {
        return submissionCount;
    }

    /**
     * Set the submission count
     * @param submissionCount The submission count
     */
    public void setSubmissionCount(int submissionCount) {
        this.submissionCount = submissionCount;
    }

    /**
     * Get the graded count
     * @return The graded count
     */
    public int getGradedCount() {
        return gradedCount;
    }

    /**
     * Set the graded count
     * @param gradedCount The graded count
     */
    public void setGradedCount(int gradedCount) {
        this.gradedCount = gradedCount;
    }
}