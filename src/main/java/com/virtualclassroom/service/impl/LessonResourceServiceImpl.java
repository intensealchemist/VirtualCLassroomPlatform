package com.virtualclassroom.service.impl;

import com.virtualclassroom.exception.AccessDeniedException;
import com.virtualclassroom.exception.ResourceNotFoundException;
import com.virtualclassroom.model.Lesson;
import com.virtualclassroom.model.LessonResource;
import com.virtualclassroom.model.ResourceType;
import com.virtualclassroom.model.User;
import com.virtualclassroom.repository.LessonRepository;
import com.virtualclassroom.repository.LessonResourceRepository;
import com.virtualclassroom.service.LessonResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of the LessonResourceService interface
 */
@Service
public class LessonResourceServiceImpl implements LessonResourceService {

    private final LessonResourceRepository lessonResourceRepository;
    private final LessonRepository lessonRepository;

    @Autowired
    public LessonResourceServiceImpl(LessonResourceRepository lessonResourceRepository,
                                     LessonRepository lessonRepository) {
        this.lessonResourceRepository = lessonResourceRepository;
        this.lessonRepository = lessonRepository;
    }

    @Override
    public List<LessonResource> getResourcesByLesson(Long lessonId) {
        return lessonResourceRepository.findByLessonId(lessonId);
    }

    @Override
    public LessonResource getResourceById(Long resourceId) {
        return lessonResourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + resourceId));
    }

    @Override
    @Transactional
    public LessonResource createResource(Long lessonId, String name, String description,
                                         String url, ResourceType type, Long fileSize, User user) {
        // Verify the lesson exists
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        // Check if user is authorized to modify the lesson
        if (!isAuthorizedToModify(lesson, user)) {
            throw new AccessDeniedException("You are not authorized to add resources to this lesson");
        }

        // Create and save the new resource
        LessonResource resource = new LessonResource();
        resource.setLesson(lesson);
        resource.setTitle(name);
        resource.setDescription(description);
        resource.setResourceUrl(url);
        resource.setResourceType(type);
        resource.setFileSize(fileSize);
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());

        return lessonResourceRepository.save(resource);
    }

    @Override
    @Transactional
    public LessonResource updateResource(Long resourceId, String name, String description,
                                         String url, ResourceType type, Long fileSize, User user) {
        // Verify the resource exists
        LessonResource resource = lessonResourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + resourceId));

        // Check if user is authorized to modify the resource
        if (!isAuthorizedToModify(resource.getLesson(), user)) {
            throw new AccessDeniedException("You are not authorized to update this resource");
        }

        // Update the resource
        resource.setName(name);
        resource.setDescription(description);
        resource.setUrl(url);
        resource.setType(type);
        resource.setFileSize(fileSize);
        resource.setUpdatedAt(LocalDateTime.now());

        return lessonResourceRepository.save(resource);
    }

    @Override
    @Transactional
    public void deleteResource(Long resourceId, User user) {
        // Verify the resource exists
        LessonResource resource = lessonResourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + resourceId));

        // Check if user is authorized to delete the resource
        if (!isAuthorizedToModify(resource.getLesson(), user)) {
            throw new AccessDeniedException("You are not authorized to delete this resource");
        }

        // Delete the resource
        lessonResourceRepository.delete(resource);
    }

    @Override
    public List<LessonResource> getResourcesByLessonAndType(Long lessonId, ResourceType type) {
        return lessonResourceRepository.findByLessonIdAndResourceType(lessonId, type);
    }

    /**
     * Helper method to check if a user is authorized to modify a lesson
     * @param lesson The lesson
     * @param user The user
     * @return True if authorized, false otherwise
     */
    private boolean isAuthorizedToModify(Lesson lesson, User user) {
        // Admins can modify any lesson
        if (user.isAdmin()) {
            return true;
        }

        // Instructors can only modify lessons in their own courses
        if (user.isInstructor()) {
            return lesson.getCourse().getInstructor().getId().equals(user.getId());
        }

        // Students cannot modify lessons
        return false;
    }
}