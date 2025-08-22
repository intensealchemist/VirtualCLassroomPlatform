package com.virtualclassroom.service.impl;

import com.virtualclassroom.exception.ResourceNotFoundException;
import com.virtualclassroom.model.Lesson;
import com.virtualclassroom.model.LessonProgress;
import com.virtualclassroom.model.User;
import com.virtualclassroom.repository.LessonProgressRepository;
import com.virtualclassroom.repository.LessonRepository;
import com.virtualclassroom.service.LessonProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of the LessonProgressService interface
 */
@Service
public class LessonProgressServiceImpl implements LessonProgressService {

    private final LessonProgressRepository lessonProgressRepository;
    private final LessonRepository lessonRepository;

    @Autowired
    public LessonProgressServiceImpl(LessonProgressRepository lessonProgressRepository, 
                                     LessonRepository lessonRepository) {
        this.lessonProgressRepository = lessonProgressRepository;
        this.lessonRepository = lessonRepository;
    }

    @Override
    public LessonProgress getProgressByLessonAndStudent(Long lessonId, Long studentId) {
        return lessonProgressRepository.findByLessonIdAndStudentId(lessonId, studentId)
                .orElse(null);
    }

    @Override
    public List<LessonProgress> getProgressByCourseAndStudent(Long courseId, Long studentId) {
        return lessonProgressRepository.findByCourseIdAndStudentId(courseId, studentId);
    }

    @Override
    @Transactional
    public LessonProgress updateProgress(Long lessonId, User student, Double progressPercentage) {
        // Validate progress percentage
        if (progressPercentage < 0 || progressPercentage > 100) {
            throw new IllegalArgumentException("Progress percentage must be between 0 and 100");
        }

        // Get or create progress record
        LessonProgress progress = getOrCreateProgress(lessonId, student);

        // Update progress
        progress.setProgressPercentage(progressPercentage);
        progress.setLastAccessedAt(LocalDateTime.now());
        progress.setUpdatedAt(LocalDateTime.now());

        // If progress is 100%, mark as completed
        if (progressPercentage >= 100 && !progress.isCompleted()) {
            progress.setCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
        }

        return lessonProgressRepository.save(progress);
    }

    @Override
    @Transactional
    public LessonProgress markLessonCompleted(Long lessonId, User student) {
        LessonProgress progress = getOrCreateProgress(lessonId, student);

        // Mark as completed
        progress.setCompleted(true);
        progress.setProgressPercentage(100.0);
        progress.setCompletedAt(LocalDateTime.now());
        progress.setLastAccessedAt(LocalDateTime.now());
        progress.setUpdatedAt(LocalDateTime.now());

        return lessonProgressRepository.save(progress);
    }

    @Override
    @Transactional
    public LessonProgress updateTimeSpent(Long lessonId, User student, Long additionalSeconds) {
        if (additionalSeconds < 0) {
            throw new IllegalArgumentException("Additional time cannot be negative");
        }

        LessonProgress progress = getOrCreateProgress(lessonId, student);

        // Update time spent
        Long currentTimeSpent = progress.getTimeSpentSeconds() != null ? progress.getTimeSpentSeconds() : 0L;
        progress.setTimeSpentSeconds(currentTimeSpent + additionalSeconds);
        progress.setLastAccessedAt(LocalDateTime.now());
        progress.setUpdatedAt(LocalDateTime.now());

        return lessonProgressRepository.save(progress);
    }

    @Override
    @Transactional
    public LessonProgress updateLastPosition(Long lessonId, User student, Long position) {
        if (position < 0) {
            throw new IllegalArgumentException("Position cannot be negative");
        }

        LessonProgress progress = getOrCreateProgress(lessonId, student);

        // Update last position
        progress.setLastPosition(position);
        progress.setLastAccessedAt(LocalDateTime.now());
        progress.setUpdatedAt(LocalDateTime.now());

        return lessonProgressRepository.save(progress);
    }

    @Override
    public Double getCourseCompletionPercentage(Long courseId, Long studentId) {
        Double averageProgress = lessonProgressRepository.getAverageProgressForCourseAndStudent(courseId, studentId);
        return averageProgress != null ? averageProgress : 0.0;
    }

    @Override
    public Integer getCompletedLessonsCount(Long courseId, Long studentId) {
        Integer completedCount = lessonProgressRepository.countCompletedLessonsByCourseAndStudent(courseId, studentId);
        return completedCount != null ? completedCount : 0;
    }

    @Override
    public Boolean isLessonCompleted(Long lessonId, Long studentId) {
        return lessonProgressRepository.findByLessonIdAndStudentId(lessonId, studentId)
                .map(LessonProgress::isCompleted)
                .orElse(false);
    }

    /**
     * Helper method to get an existing progress record or create a new one
     * @param lessonId The lesson ID
     * @param student The student
     * @return The lesson progress record
     */
    private LessonProgress getOrCreateProgress(Long lessonId, User student) {
        return lessonProgressRepository.findByLessonIdAndStudentId(lessonId, student.getId())
                .orElseGet(() -> {
                    // Create new progress record
                    Lesson lesson = lessonRepository.findById(lessonId)
                            .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

                    LessonProgress newProgress = new LessonProgress();
                    newProgress.setLesson(lesson);
                    newProgress.setStudent(student);
                    newProgress.setProgressPercentage(0.0);
                    newProgress.setCompleted(false);
                    newProgress.setTimeSpentSeconds(0L);
                    newProgress.setLastPosition(0L);
                    newProgress.setCreatedAt(LocalDateTime.now());
                    newProgress.setUpdatedAt(LocalDateTime.now());
                    newProgress.setStartedAt(LocalDateTime.now());
                    newProgress.setLastAccessedAt(LocalDateTime.now());
                    
                    return lessonProgressRepository.save(newProgress);
                });
    }
}