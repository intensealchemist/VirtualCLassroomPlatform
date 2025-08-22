package com.virtualclassroom.service;

import com.virtualclassroom.model.LessonResource;
import com.virtualclassroom.model.ResourceType;
import com.virtualclassroom.model.User;

import java.util.List;

/**
 * Service interface for lesson resource operations
 */
public interface LessonResourceService {

    /**
     * Get all resources for a lesson
     * @param lessonId The lesson ID
     * @return List of lesson resources
     */
    List<LessonResource> getResourcesByLesson(Long lessonId);

    /**
     * Get a specific resource by ID
     * @param resourceId The resource ID
     * @return The lesson resource
     */
    LessonResource getResourceById(Long resourceId);

    /**
     * Create a new resource for a lesson
     * @param lessonId The lesson ID
     * @param name The resource name
     * @param description The resource description
     * @param url The resource URL
     * @param type The resource type
     * @param fileSize The file size in bytes (optional)
     * @param user The authenticated user
     * @return The created resource
     */
    LessonResource createResource(Long lessonId, String name, String description, 
                                  String url, ResourceType type, Long fileSize, User user);

    /**
     * Update an existing resource
     * @param resourceId The resource ID
     * @param name The updated resource name
     * @param description The updated resource description
     * @param url The updated resource URL
     * @param type The updated resource type
     * @param fileSize The updated file size in bytes (optional)
     * @param user The authenticated user
     * @return The updated resource
     */
    LessonResource updateResource(Long resourceId, String name, String description, 
                                  String url, ResourceType type, Long fileSize, User user);

    /**
     * Delete a resource
     * @param resourceId The resource ID
     * @param user The authenticated user
     */
    void deleteResource(Long resourceId, User user);

    /**
     * Get resources by type for a lesson
     * @param lessonId The lesson ID
     * @param type The resource type
     * @return List of lesson resources of the specified type
     */
    List<LessonResource> getResourcesByLessonAndType(Long lessonId, ResourceType type);
}