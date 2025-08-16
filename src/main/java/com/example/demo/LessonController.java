package com.example.demo;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lessons")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LessonController {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Lesson>> getLessonsByCourse(@PathVariable Long courseId,
                                                          @RequestParam(defaultValue = "false") boolean publishedOnly) {
        List<Lesson> lessons;
        if (publishedOnly) {
            lessons = lessonRepository.findByCourseIdAndIsPublishedTrue(courseId);
        } else {
            lessons = lessonRepository.findByCourseIdOrderByLessonOrder(courseId);
        }
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lesson> getLessonById(@PathVariable Long id) {
        Optional<Lesson> lesson = lessonRepository.findById(id);
        return lesson.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<Lesson> createLesson(@Valid @RequestBody Lesson lesson,
                                             @AuthenticationPrincipal CustomUserDetails currentUser) {
        // Verify the course exists and user has permission
        Optional<Course> courseOptional = courseRepository.findById(lesson.getCourse().getId());
        if (courseOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Course course = courseOptional.get();
        if (!course.getInstructor().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.status(403).build();
        }

        lesson.setCourse(course);
        
        // Auto-set lesson order if not provided
        if (lesson.getLessonOrder() == null) {
            Long lessonCount = lessonRepository.countLessonsByCourse(course.getId());
            lesson.setLessonOrder(lessonCount.intValue() + 1);
        }

        Lesson savedLesson = lessonRepository.save(lesson);
        return ResponseEntity.ok(savedLesson);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<Lesson> updateLesson(@PathVariable Long id,
                                             @Valid @RequestBody Lesson lessonDetails,
                                             @AuthenticationPrincipal CustomUserDetails currentUser) {
        Optional<Lesson> lessonOptional = lessonRepository.findById(id);
        if (lessonOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Lesson lesson = lessonOptional.get();
        
        // Check permissions
        if (!lesson.getCourse().getInstructor().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.status(403).build();
        }

        lesson.setTitle(lessonDetails.getTitle());
        lesson.setContent(lessonDetails.getContent());
        lesson.setVideoUrl(lessonDetails.getVideoUrl());
        lesson.setDurationMinutes(lessonDetails.getDurationMinutes());
        lesson.setLessonOrder(lessonDetails.getLessonOrder());
        lesson.setPublished(lessonDetails.isPublished());

        Lesson updatedLesson = lessonRepository.save(lesson);
        return ResponseEntity.ok(updatedLesson);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteLesson(@PathVariable Long id,
                                        @AuthenticationPrincipal CustomUserDetails currentUser) {
        Optional<Lesson> lessonOptional = lessonRepository.findById(id);
        if (lessonOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Lesson lesson = lessonOptional.get();
        
        // Check permissions
        if (!lesson.getCourse().getInstructor().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.status(403).build();
        }

        lessonRepository.delete(lesson);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<Lesson> togglePublishStatus(@PathVariable Long id,
                                                    @AuthenticationPrincipal CustomUserDetails currentUser) {
        Optional<Lesson> lessonOptional = lessonRepository.findById(id);
        if (lessonOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Lesson lesson = lessonOptional.get();
        
        // Check permissions
        if (!lesson.getCourse().getInstructor().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.status(403).build();
        }

        lesson.setPublished(!lesson.isPublished());
        Lesson updatedLesson = lessonRepository.save(lesson);
        return ResponseEntity.ok(updatedLesson);
    }

    @GetMapping("/instructor/my-lessons")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<Lesson>> getMyLessons(@AuthenticationPrincipal CustomUserDetails currentUser) {
        List<Lesson> lessons = lessonRepository.findByInstructorId(currentUser.getId());
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Lesson>> searchLessons(@RequestParam String keyword) {
        List<Lesson> lessons = lessonRepository.findByKeyword(keyword);
        return ResponseEntity.ok(lessons);
    }
}
