package com.virtualclassroom.controller;

import com.virtualclassroom.dto.LessonProgressDTO;
import com.virtualclassroom.mapper.LessonProgressMapper;
import com.virtualclassroom.model.LessonProgress;
import com.virtualclassroom.model.User;
import com.virtualclassroom.service.LessonProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing lesson progress
 */
@RestController
@RequestMapping("/api/progress")
public class LessonProgressController {

    private final LessonProgressService lessonProgressService;
    private final LessonProgressMapper lessonProgressMapper;

    @Autowired
    public LessonProgressController(LessonProgressService lessonProgressService,
                                    LessonProgressMapper lessonProgressMapper) {
        this.lessonProgressService = lessonProgressService;
        this.lessonProgressMapper = lessonProgressMapper;
    }

    /**
     * Get a student's progress for a specific lesson
     * @param lessonId The lesson ID
     * @param user The authenticated user
     * @return The lesson progress DTO
     */
    @GetMapping("/lesson/{lessonId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<LessonProgressDTO> getProgressForLesson(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal User user) {
        
        LessonProgress progress = lessonProgressService.getProgressByLessonAndStudent(lessonId, user.getId());
        if (progress == null) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(lessonProgressMapper.toDTO(progress));
    }

    /**
     * Get all progress records for a student in a course
     * @param courseId The course ID
     * @param user The authenticated user
     * @return List of lesson progress DTOs
     */
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<LessonProgressDTO>> getProgressForCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User user) {
        
        List<LessonProgress> progressList = lessonProgressService.getProgressByCourseAndStudent(courseId, user.getId());
        if (progressList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(lessonProgressMapper.toDTOList(progressList));
    }

    /**
     * Update a student's progress for a lesson
     * @param lessonId The lesson ID
     * @param progressPercentage The progress percentage (0-100)
     * @param user The authenticated user
     * @return The updated progress DTO
     */
    @PostMapping("/lesson/{lessonId}/progress")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public ResponseEntity<LessonProgressDTO> updateProgress(
            @PathVariable Long lessonId,
            @RequestParam Double progressPercentage,
            @AuthenticationPrincipal User user) {
        
        LessonProgress updatedProgress = lessonProgressService.updateProgress(lessonId, user, progressPercentage);
        return ResponseEntity.ok(lessonProgressMapper.toDTO(updatedProgress));
    }

    /**
     * Mark a lesson as completed for a student
     * @param lessonId The lesson ID
     * @param user The authenticated user
     * @return The updated progress DTO
     */
    @PostMapping("/lesson/{lessonId}/complete")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public ResponseEntity<LessonProgressDTO> markLessonCompleted(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal User user) {
        
        LessonProgress completedProgress = lessonProgressService.markLessonCompleted(lessonId, user);
        return ResponseEntity.ok(lessonProgressMapper.toDTO(completedProgress));
    }

    /**
     * Update the time spent on a lesson by a student
     * @param lessonId The lesson ID
     * @param additionalSeconds Additional seconds spent
     * @param user The authenticated user
     * @return The updated progress DTO
     */
    @PostMapping("/lesson/{lessonId}/time")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public ResponseEntity<LessonProgressDTO> updateTimeSpent(
            @PathVariable Long lessonId,
            @RequestParam Long additionalSeconds,
            @AuthenticationPrincipal User user) {
        
        LessonProgress updatedProgress = lessonProgressService.updateTimeSpent(lessonId, user, additionalSeconds);
        return ResponseEntity.ok(lessonProgressMapper.toDTO(updatedProgress));
    }

    /**
     * Update the last position in a video/audio lesson
     * @param lessonId The lesson ID
     * @param position The position in seconds
     * @param user The authenticated user
     * @return The updated progress DTO
     */
    @PostMapping("/lesson/{lessonId}/position")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public ResponseEntity<LessonProgressDTO> updateLastPosition(
            @PathVariable Long lessonId,
            @RequestParam Long position,
            @AuthenticationPrincipal User user) {
        
        LessonProgress updatedProgress = lessonProgressService.updateLastPosition(lessonId, user, position);
        return ResponseEntity.ok(lessonProgressMapper.toDTO(updatedProgress));
    }

    /**
     * Get the completion percentage for a course by a student
     * @param courseId The course ID
     * @param user The authenticated user
     * @return The completion percentage
     */
    @GetMapping("/course/{courseId}/completion")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Double> getCourseCompletionPercentage(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User user) {
        
        Double completionPercentage = lessonProgressService.getCourseCompletionPercentage(courseId, user.getId());
        return ResponseEntity.ok(completionPercentage);
    }

    /**
     * Get the number of completed lessons in a course by a student
     * @param courseId The course ID
     * @param user The authenticated user
     * @return The number of completed lessons
     */
    @GetMapping("/course/{courseId}/completed-count")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Integer> getCompletedLessonsCount(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User user) {
        
        Integer completedCount = lessonProgressService.getCompletedLessonsCount(courseId, user.getId());
        return ResponseEntity.ok(completedCount);
    }

    /**
     * Check if a student has completed a specific lesson
     * @param lessonId The lesson ID
     * @param user The authenticated user
     * @return True if completed, false otherwise
     */
    @GetMapping("/lesson/{lessonId}/is-completed")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Boolean> isLessonCompleted(
            @PathVariable Long lessonId,
            @AuthenticationPrincipal User user) {
        
        Boolean isCompleted = lessonProgressService.isLessonCompleted(lessonId, user.getId());
        return ResponseEntity.ok(isCompleted);
    }
}