package com.virtualclassroom.service;

import com.virtualclassroom.model.Lesson;
import com.virtualclassroom.model.User;

import java.util.List;

/**
 * Service interface for lesson-related operations
 */
public interface LessonService {

    /**
     * Get all lessons for a course
     * @param courseId The course ID
     * @return The list of lessons
     */
    List<Lesson> getLessonsByCourse(Long courseId);

    /**
     * Get a lesson by ID
     * @param lessonId The lesson ID
     * @param courseId The course ID
     * @return The lesson
     */
    Lesson getLessonById(Long lessonId, Long courseId);

    /**
     * Create a new lesson
     * @param courseId The course ID
     * @param lesson The lesson data
     * @param user The authenticated user
     * @return The created lesson
     */
    Lesson createLesson(Long courseId, Lesson lesson, User user);

    /**
     * Update a lesson
     * @param courseId The course ID
     * @param lesson The updated lesson data
     * @param user The authenticated user
     * @return The updated lesson
     */
    Lesson updateLesson(Long courseId, Lesson lesson, User user);

    /**
     * Delete a lesson
     * @param lessonId The lesson ID
     * @param courseId The course ID
     * @param user The authenticated user
     */
    void deleteLesson(Long lessonId, Long courseId, User user);

    /**
     * Update a lesson's video URL
     * @param lessonId The lesson ID
     * @param courseId The course ID
     * @param videoUrl The video URL
     * @param user The authenticated user
     * @return The updated lesson
     */
    Lesson updateLessonVideo(Long lessonId, Long courseId, String videoUrl, User user);

    /**
     * Update a lesson's attachment URL
     * @param lessonId The lesson ID
     * @param courseId The course ID
     * @param attachmentUrl The attachment URL
     * @param user The authenticated user
     * @return The updated lesson
     */
    Lesson updateLessonAttachment(Long lessonId, Long courseId, String attachmentUrl, User user);

    /**
     * Reorder lessons in a course
     * @param courseId The course ID
     * @param lessonIds The ordered list of lesson IDs
     * @param user The authenticated user
     * @return The list of reordered lessons
     */
    List<Lesson> reorderLessons(Long courseId, List<Long> lessonIds, User user);

    /**
     * Reorder a single lesson
     * @param lessonId The lesson ID
     * @param courseId The course ID
     * @param newOrder The new order index
     * @param user The authenticated user
     * @return The updated lesson
     */
    Lesson reorderLesson(Long lessonId, Long courseId, int newOrder, User user);

    /**
     * Publish or unpublish a lesson
     * @param lessonId The lesson ID
     * @param courseId The course ID
     * @param publish Whether to publish or unpublish
     * @param user The authenticated user
     * @return The updated lesson
     */
    Lesson publishLesson(Long lessonId, Long courseId, boolean publish, User user);
}