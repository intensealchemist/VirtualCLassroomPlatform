package com.virtualclassroom.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Lesson Resource
 */
public class LessonResourceDTO {

    private Long id;
    private String title;
    private String description;
    private String resourceUrl;
    private String resourceType;
    private Long lessonId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Default constructor
     */
    public LessonResourceDTO() {
    }

    /**
     * Get the resource ID
     * @return The resource ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the resource ID
     * @param id The resource ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the resource title
     * @return The resource title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the resource title
     * @param title The resource title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the resource description
     * @return The resource description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the resource description
     * @param description The resource description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the resource URL
     * @return The resource URL
     */
    public String getResourceUrl() {
        return resourceUrl;
    }

    /**
     * Set the resource URL
     * @param resourceUrl The resource URL
     */
    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    /**
     * Get the resource type
     * @return The resource type
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * Set the resource type
     * @param resourceType The resource type
     */
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * Get the lesson ID
     * @return The lesson ID
     */
    public Long getLessonId() {
        return lessonId;
    }

    /**
     * Set the lesson ID
     * @param lessonId The lesson ID
     */
    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
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
}