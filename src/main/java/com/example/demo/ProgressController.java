package com.example.demo;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/progress")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProgressController {

    @Autowired
    private LessonProgressRepository lessonProgressRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private CourseEnrollmentRepository enrollmentRepository;

    @PostMapping("/lesson/{lessonId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<LessonProgress> updateLessonProgress(@PathVariable Long lessonId,
                                                             @Valid @RequestBody LessonProgressRequest request,
                                                             @AuthenticationPrincipal CustomUserDetails currentUser) {
        // Verify lesson exists
        Optional<Lesson> lessonOptional = lessonRepository.findById(lessonId);
        if (lessonOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Lesson lesson = lessonOptional.get();
        
        // Check if student is enrolled in the course
        if (!enrollmentRepository.existsByStudentIdAndCourseId(currentUser.getId(), lesson.getCourse().getId())) {
            return ResponseEntity.status(403).body(null);
        }

        // Find or create lesson progress
        Optional<LessonProgress> progressOptional = 
            lessonProgressRepository.findByStudentIdAndLessonId(currentUser.getId(), lessonId);
        
        LessonProgress progress;
        if (progressOptional.isPresent()) {
            progress = progressOptional.get();
        } else {
            progress = new LessonProgress(currentUser.getUser(), lesson);
        }

        progress.setCompletionPercentage(request.getCompletionPercentage());
        progress.setTimeSpentMinutes(request.getTimeSpentMinutes());
        progress.setLastAccessed(LocalDateTime.now());

        LessonProgress savedProgress = lessonProgressRepository.save(progress);
        
        // Update overall course progress
        updateCourseProgress(currentUser.getId(), lesson.getCourse().getId());
        
        return ResponseEntity.ok(savedProgress);
    }

    @GetMapping("/lesson/{lessonId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<LessonProgress> getLessonProgress(@PathVariable Long lessonId,
                                                          @AuthenticationPrincipal CustomUserDetails currentUser) {
        Optional<LessonProgress> progress = 
            lessonProgressRepository.findByStudentIdAndLessonId(currentUser.getId(), lessonId);
        
        return progress.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<LessonProgress>> getCourseProgress(@PathVariable Long courseId,
                                                                @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<LessonProgress> progress = 
            lessonProgressRepository.findByStudentAndCourse(currentUser.getId(), courseId);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/my-progress")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<LessonProgress>> getMyProgress(@AuthenticationPrincipal CustomUserDetails currentUser) {
        List<LessonProgress> progress = lessonProgressRepository.findByStudentId(currentUser.getId());
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/incomplete")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<LessonProgress>> getIncompleteProgress(@AuthenticationPrincipal CustomUserDetails currentUser) {
        List<LessonProgress> progress = lessonProgressRepository.findIncompleteByStudent(currentUser.getId());
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/course/{courseId}/stats")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<CourseProgressStats> getCourseProgressStats(@PathVariable Long courseId,
                                                                     @AuthenticationPrincipal CustomUserDetails currentUser) {
        Double averageCompletion = lessonProgressRepository.getAverageCompletionByCourse(courseId);
        Long totalEnrollments = enrollmentRepository.countActiveEnrollmentsByCourse(courseId);
        
        CourseProgressStats stats = new CourseProgressStats(courseId, averageCompletion, totalEnrollments);
        return ResponseEntity.ok(stats);
    }

    private void updateCourseProgress(Long studentId, Long courseId) {
        // Calculate overall course completion percentage
        List<Lesson> courseLessons = lessonRepository.findByCourseIdAndIsPublishedTrue(courseId);
        if (courseLessons.isEmpty()) return;

        List<LessonProgress> studentProgress = 
            lessonProgressRepository.findByStudentAndCourse(studentId, courseId);

        double totalCompletion = 0.0;
        int completedLessons = 0;

        for (Lesson lesson : courseLessons) {
            LessonProgress progress = studentProgress.stream()
                .filter(p -> p.getLesson().getId().equals(lesson.getId()))
                .findFirst()
                .orElse(null);

            if (progress != null) {
                totalCompletion += progress.getCompletionPercentage();
                if (progress.isCompleted()) {
                    completedLessons++;
                }
            }
        }

        double courseCompletionPercentage = totalCompletion / courseLessons.size();

        // Update course enrollment progress
        Optional<CourseEnrollment> enrollmentOptional = 
            enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);
        
        if (enrollmentOptional.isPresent()) {
            CourseEnrollment enrollment = enrollmentOptional.get();
            enrollment.setCompletionPercentage(courseCompletionPercentage);
            enrollment.setLastAccessed(LocalDateTime.now());
            enrollmentRepository.save(enrollment);
        }
    }
}
