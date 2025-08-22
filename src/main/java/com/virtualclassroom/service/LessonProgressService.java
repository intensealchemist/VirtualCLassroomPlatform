package com.virtualclassroom.service;

import com.virtualclassroom.model.Lesson;
import com.virtualclassroom.model.LessonProgress;
import com.virtualclassroom.model.User;

import java.util.List;

/**
 * Service interface for lesson progress tracking
 */
public interface LessonProgressService {

    /**
     * Get a student's progress for a specific lesson
     * @param lessonId The lesson ID
     * @param studentId The student ID
     * @return The lesson progress record
     */
    LessonProgress getProgressByLessonAndStudent(Long lessonId, Long studentId);

    /**
     * Get all progress records for a student in a course
     * @param courseId The course ID
     * @param studentId The student ID
     * @return List of lesson progress records
     */
    List<LessonProgress> getProgressByCourseAndStudent(Long courseId, Long studentId);

    /**
     * Update a student's progress for a lesson
     * @param lessonId The lesson ID
     * @param student The student
     * @param progressPercentage The progress percentage (0-100)
     * @return The updated progress record
     */
    LessonProgress updateProgress(Long lessonId, User student, Double progressPercentage);

    /**
     * Mark a lesson as completed for a student
     * @param lessonId The lesson ID
     * @param student The student
     * @return The updated progress record
     */
    LessonProgress markLessonCompleted(Long lessonId, User student);

    /**
     * Update the time spent on a lesson by a student
     * @param lessonId The lesson ID
     * @param student The student
     * @param additionalSeconds Additional seconds spent
     * @return The updated progress record
     */
    LessonProgress updateTimeSpent(Long lessonId, User student, Long additionalSeconds);

    /**
     * Update the last position in a video/audio lesson
     * @param lessonId The lesson ID
     * @param student The student
     * @param position The position in seconds
     * @return The updated progress record
     */
    LessonProgress updateLastPosition(Long lessonId, User student, Long position);

    /**
     * Get the completion percentage for a course by a student
     * @param courseId The course ID
     * @param studentId The student ID
     * @return The completion percentage (0-100)
     */
    Double getCourseCompletionPercentage(Long courseId, Long studentId);

    /**
     * Get the number of completed lessons in a course by a student
     * @param courseId The course ID
     * @param studentId The student ID
     * @return The number of completed lessons
     */
    Integer getCompletedLessonsCount(Long courseId, Long studentId);

    /**
     * Check if a student has completed a specific lesson
     * @param lessonId The lesson ID
     * @param studentId The student ID
     * @return True if completed, false otherwise
     */
    Boolean isLessonCompleted(Long lessonId, Long studentId);
}