package com.virtualclassroom.service.impl;

import com.virtualclassroom.exception.AccessDeniedException;
import com.virtualclassroom.exception.ResourceNotFoundException;
import com.virtualclassroom.model.Course;
import com.virtualclassroom.model.Lesson;
import com.virtualclassroom.model.User;
import com.virtualclassroom.repository.CourseRepository;
import com.virtualclassroom.repository.LessonRepository;
import com.virtualclassroom.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of the LessonService interface
 */
@Service
public class LessonServiceImpl implements LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public List<Lesson> getLessonsByCourse(Long courseId) {
        // Verify course exists
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with id: " + courseId);
        }
        return lessonRepository.findByCourseIdOrderByLessonOrderAsc(courseId);
    }

    @Override
    public Lesson getLessonById(Long lessonId, Long courseId) {
        return lessonRepository.findByIdAndCourseId(lessonId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Lesson not found with id: " + lessonId + " for course: " + courseId));
    }

    @Override
    @Transactional
    public Lesson createLesson(Long courseId, Lesson lesson, User user) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // Check if user is the instructor of the course or an admin
        if (!isAuthorizedToModify(course, user)) {
            throw new AccessDeniedException("You are not authorized to add lessons to this course");
        }

        // Set the course for the lesson
        lesson.setCourse(course);

        // Set the order for the new lesson (append to the end)
        int maxOrder = lessonRepository.findMaxLessonOrderByCourseId(courseId);
        lesson.setLessonOrder(maxOrder + 1);

        // Set creation timestamps
        LocalDateTime now = LocalDateTime.now();
        lesson.setCreatedAt(now);
        lesson.setUpdatedAt(now);

        return lessonRepository.save(lesson);
    }

    @Override
    @Transactional
    public Lesson updateLesson(Long courseId, Lesson lesson, User user) {
        // Verify the lesson exists and belongs to the specified course
        Lesson existingLesson = getLessonById(lesson.getId(), courseId);
        Course course = existingLesson.getCourse();

        // Check if user is authorized to modify the lesson
        if (!isAuthorizedToModify(course, user)) {
            throw new AccessDeniedException("You are not authorized to update this lesson");
        }

        // Update the lesson properties
        existingLesson.setTitle(lesson.getTitle());
        existingLesson.setDescription(lesson.getDescription());
        existingLesson.setContent(lesson.getContent());
        existingLesson.setType(lesson.getType());
        existingLesson.setDurationMinutes(lesson.getDurationMinutes());
        existingLesson.setIsPreview(lesson.getIsPreview());
        
        // Don't update these properties directly through this method
        // - lessonOrder (use reorderLesson)
        // - isPublished (use publishLesson)
        // - videoUrl (use updateLessonVideo)
        // - documentUrl (use updateLessonAttachment)

        // Update timestamp
        existingLesson.setUpdatedAt(LocalDateTime.now());

        return lessonRepository.save(existingLesson);
    }

    @Override
    @Transactional
    public void deleteLesson(Long lessonId, Long courseId, User user) {
        // Verify the lesson exists and belongs to the specified course
        Lesson lesson = getLessonById(lessonId, courseId);
        Course course = lesson.getCourse();

        // Check if user is authorized to delete the lesson
        if (!isAuthorizedToModify(course, user)) {
            throw new AccessDeniedException("You are not authorized to delete this lesson");
        }

        // Get the order of the lesson to be deleted
        int deletedOrder = lesson.getLessonOrder();

        // Delete the lesson
        lessonRepository.delete(lesson);

        // Shift the order of subsequent lessons
        lessonRepository.shiftLessonOrders(courseId, deletedOrder + 1, -1);
    }

    @Override
    @Transactional
    public Lesson updateLessonVideo(Long lessonId, Long courseId, String videoUrl, User user) {
        // Verify the lesson exists and belongs to the specified course
        Lesson lesson = getLessonById(lessonId, courseId);

        // Check if user is authorized to modify the lesson
        if (!isAuthorizedToModify(lesson.getCourse(), user)) {
            throw new AccessDeniedException("You are not authorized to update this lesson");
        }

        // Update the video URL
        lesson.setVideoUrl(videoUrl);
        lesson.setUpdatedAt(LocalDateTime.now());

        return lessonRepository.save(lesson);
    }

    @Override
    @Transactional
    public Lesson updateLessonAttachment(Long lessonId, Long courseId, String attachmentUrl, User user) {
        // Verify the lesson exists and belongs to the specified course
        Lesson lesson = getLessonById(lessonId, courseId);

        // Check if user is authorized to modify the lesson
        if (!isAuthorizedToModify(lesson.getCourse(), user)) {
            throw new AccessDeniedException("You are not authorized to update this lesson");
        }

        // Update the document URL
        lesson.setDocumentUrl(attachmentUrl);
        lesson.setUpdatedAt(LocalDateTime.now());

        return lessonRepository.save(lesson);
    }

    @Override
    @Transactional
    public List<Lesson> reorderLessons(Long courseId, List<Long> lessonIds, User user) {
        // Verify the course exists
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // Check if user is authorized to modify the course
        if (!isAuthorizedToModify(course, user)) {
            throw new AccessDeniedException("You are not authorized to reorder lessons in this course");
        }

        // Verify all lessons exist and belong to the course
        List<Lesson> courseLessons = lessonRepository.findByCourseIdOrderByLessonOrderAsc(courseId);
        if (courseLessons.size() != lessonIds.size()) {
            throw new IllegalArgumentException("The provided lesson IDs do not match the lessons in the course");
        }

        // Update the order of each lesson
        for (int i = 0; i < lessonIds.size(); i++) {
            Long lessonId = lessonIds.get(i);
            int newOrder = i + 1; // Order starts from 1
            lessonRepository.updateLessonOrder(lessonId, newOrder);
        }
        
        // Return the updated lessons in their new order
        return lessonRepository.findByCourseIdOrderByLessonOrderAsc(courseId);
    }

    @Override
    @Transactional
    public Lesson reorderLesson(Long lessonId, Long courseId, int newOrder, User user) {
        // Verify the lesson exists and belongs to the specified course
        Lesson lesson = getLessonById(lessonId, courseId);
        Course course = lesson.getCourse();

        // Check if user is authorized to modify the lesson
        if (!isAuthorizedToModify(course, user)) {
            throw new AccessDeniedException("You are not authorized to reorder this lesson");
        }

        // Get the current order of the lesson
        int currentOrder = lesson.getLessonOrder();

        // Validate the new order
        int maxOrder = lessonRepository.findMaxLessonOrderByCourseId(courseId);
        if (newOrder < 1 || newOrder > maxOrder) {
            throw new IllegalArgumentException("Invalid order value. Must be between 1 and " + maxOrder);
        }

        // If the order hasn't changed, return the lesson as is
        if (currentOrder == newOrder) {
            return lesson;
        }

        // Update the orders of affected lessons
        if (currentOrder < newOrder) {
            // Moving down: shift lessons between current and new position up by 1
            lessonRepository.shiftLessonOrders(courseId, currentOrder + 1, -1);
            lessonRepository.shiftLessonOrders(courseId, newOrder + 1, 1);
        } else {
            // Moving up: shift lessons between new and current position down by 1
            lessonRepository.shiftLessonOrders(courseId, newOrder, 1);
            lessonRepository.shiftLessonOrders(courseId, currentOrder + 1, -1);
        }

        // Update the order of the target lesson
        lesson.setLessonOrder(newOrder);
        lesson.setUpdatedAt(LocalDateTime.now());

        return lessonRepository.save(lesson);
    }

    @Override
    @Transactional
    public Lesson publishLesson(Long lessonId, Long courseId, boolean publish, User user) {
        // Verify the lesson exists and belongs to the specified course
        Lesson lesson = getLessonById(lessonId, courseId);

        // Check if user is authorized to modify the lesson
        if (!isAuthorizedToModify(lesson.getCourse(), user)) {
            throw new AccessDeniedException("You are not authorized to publish/unpublish this lesson");
        }

        // Update the published status
        lesson.setIsPublished(publish);
        lesson.setUpdatedAt(LocalDateTime.now());

        return lessonRepository.save(lesson);
    }

    /**
     * Check if a user is authorized to modify a course (instructor or admin)
     * @param course The course
     * @param user The user
     * @return True if authorized, false otherwise
     */
    private boolean isAuthorizedToModify(Course course, User user) {
        return user.isAdmin() ||
               (course.getInstructor() != null && course.getInstructor().getId().equals(user.getId()));
    }
}