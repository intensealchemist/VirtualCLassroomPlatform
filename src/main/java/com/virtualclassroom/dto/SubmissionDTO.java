package com.virtualclassroom.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Submission
 */
public class SubmissionDTO {

    private Long id;
    private Long assignmentId;
    private String assignmentTitle;
    private Long studentId;
    private String studentName;
    private String content;
    private String attachmentUrl;
    private Integer grade;
    private String feedback;
    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;
    private boolean isLate;
    private Long courseId;
    private String courseName;

    /**
     * Default constructor
     */
    public SubmissionDTO() {
    }

    /**
     * Get the submission ID
     * @return The submission ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the submission ID
     * @param id The submission ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the assignment ID
     * @return The assignment ID
     */
    public Long getAssignmentId() {
        return assignmentId;
    }

    /**
     * Set the assignment ID
     * @param assignmentId The assignment ID
     */
    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    /**
     * Get the assignment title
     * @return The assignment title
     */
    public String getAssignmentTitle() {
        return assignmentTitle;
    }

    /**
     * Set the assignment title
     * @param assignmentTitle The assignment title
     */
    public void setAssignmentTitle(String assignmentTitle) {
        this.assignmentTitle = assignmentTitle;
    }

    /**
     * Get the student ID
     * @return The student ID
     */
    public Long getStudentId() {
        return studentId;
    }

    /**
     * Set the student ID
     * @param studentId The student ID
     */
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    /**
     * Get the student name
     * @return The student name
     */
    public String getStudentName() {
        return studentName;
    }

    /**
     * Set the student name
     * @param studentName The student name
     */
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    /**
     * Get the submission content
     * @return The submission content
     */
    public String getContent() {
        return content;
    }

    /**
     * Set the submission content
     * @param content The submission content
     */
    public void setContent(String content) {
        this.content = content;
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
     * Get the grade
     * @return The grade
     */
    public Integer getGrade() {
        return grade;
    }

    /**
     * Set the grade
     * @param grade The grade
     */
    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    /**
     * Get the feedback
     * @return The feedback
     */
    public String getFeedback() {
        return feedback;
    }

    /**
     * Set the feedback
     * @param feedback The feedback
     */
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    /**
     * Get the submission timestamp
     * @return The submission timestamp
     */
    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    /**
     * Set the submission timestamp
     * @param submittedAt The submission timestamp
     */
    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    /**
     * Get the grading timestamp
     * @return The grading timestamp
     */
    public LocalDateTime getGradedAt() {
        return gradedAt;
    }

    /**
     * Set the grading timestamp
     * @param gradedAt The grading timestamp
     */
    public void setGradedAt(LocalDateTime gradedAt) {
        this.gradedAt = gradedAt;
    }

    /**
     * Check if the submission is late
     * @return True if late, false otherwise
     */
    public boolean isLate() {
        return isLate;
    }

    /**
     * Set the late status
     * @param late The late status
     */
    public void setLate(boolean late) {
        isLate = late;
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
}