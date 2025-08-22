package com.virtualclassroom.dto;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Data Transfer Object for Lesson
 */
public class LessonDTO {

    private Long id;
    private String title;
    private String description;
    private String content;
    private Long courseId;
    private String courseName;
    private Integer orderIndex;
    private String videoUrl;
    private String attachmentUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isPublished;
    private Set<LessonResourceDTO> resources;

    /**
     * Default constructor
     */
    public LessonDTO() {
    }

    /**
     * Get the lesson ID
     * @return The lesson ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the lesson ID
     * @param id The lesson ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the lesson title
     * @return The lesson title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the lesson title
     * @param title The lesson title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the lesson description
     * @return The lesson description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the lesson description
     * @param description The lesson description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the lesson content
     * @return The lesson content
     */
    public String getContent() {
        return content;
    }

    /**
     * Set the lesson content
     * @param content The lesson content
     */
    public void setContent(String content) {
        this.content = content;
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
     * Get the order index
     * @return The order index
     */
    public Integer getOrderIndex() {
        return orderIndex;
    }

    /**
     * Set the order index
     * @param orderIndex The order index
     */
    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    /**
     * Get the video URL
     * @return The video URL
     */
    public String getVideoUrl() {
        return videoUrl;
    }

    /**
     * Set the video URL
     * @param videoUrl The video URL
     */
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
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
     * Check if the lesson is published
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
     * Get the lesson resources
     * @return The lesson resources
     */
    public Set<LessonResourceDTO> getResources() {
        return resources;
    }

    /**
     * Set the lesson resources
     * @param resources The lesson resources
     */
    public void setResources(Set<LessonResourceDTO> resources) {
        this.resources = resources;
    }
}