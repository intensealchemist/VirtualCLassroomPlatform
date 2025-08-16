package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EnrollmentController {

    @Autowired
    private CourseEnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @PostMapping("/enroll/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> enrollInCourse(@PathVariable Long courseId,
                                          @AuthenticationPrincipal CustomUserDetails currentUser) {
        // Check if course exists
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Course course = courseOptional.get();
        
        // Check if already enrolled
        if (enrollmentRepository.existsByStudentIdAndCourseId(currentUser.getId(), courseId)) {
            return ResponseEntity.badRequest().body("Already enrolled in this course");
        }

        // Check enrollment limit
        if (course.getEnrollmentLimit() != null) {
            Long currentEnrollments = enrollmentRepository.countActiveEnrollmentsByCourse(courseId);
            if (currentEnrollments >= course.getEnrollmentLimit()) {
                return ResponseEntity.badRequest().body("Course enrollment limit reached");
            }
        }

        CourseEnrollment enrollment = new CourseEnrollment(currentUser.getUser(), course);
        enrollmentRepository.save(enrollment);
        
        return ResponseEntity.ok("Successfully enrolled in course");
    }

    @DeleteMapping("/unenroll/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> unenrollFromCourse(@PathVariable Long courseId,
                                              @AuthenticationPrincipal CustomUserDetails currentUser) {
        Optional<CourseEnrollment> enrollmentOptional = 
            enrollmentRepository.findByStudentIdAndCourseId(currentUser.getId(), courseId);
        
        if (enrollmentOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CourseEnrollment enrollment = enrollmentOptional.get();
        enrollment.setActive(false);
        enrollmentRepository.save(enrollment);
        
        return ResponseEntity.ok("Successfully unenrolled from course");
    }

    @GetMapping("/my-courses")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<CourseEnrollment>> getMyEnrollments(@AuthenticationPrincipal CustomUserDetails currentUser) {
        List<CourseEnrollment> enrollments = enrollmentRepository.findActiveEnrollmentsByStudent(currentUser.getId());
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/course/{courseId}/students")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<CourseEnrollment>> getCourseEnrollments(@PathVariable Long courseId,
                                                                     @AuthenticationPrincipal CustomUserDetails currentUser) {
        // Verify instructor has permission for this course
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Course course = courseOptional.get();
        if (!course.getInstructor().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.status(403).build();
        }

        List<CourseEnrollment> enrollments = enrollmentRepository.findByCourseIdAndIsActiveTrue(courseId);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/instructor/all-enrollments")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<CourseEnrollment>> getInstructorEnrollments(@AuthenticationPrincipal CustomUserDetails currentUser) {
        List<CourseEnrollment> enrollments = enrollmentRepository.findEnrollmentsByInstructor(currentUser.getId());
        return ResponseEntity.ok(enrollments);
    }

    @PutMapping("/{enrollmentId}/progress")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<CourseEnrollment> updateProgress(@PathVariable Long enrollmentId,
                                                         @RequestBody ProgressUpdateRequest request,
                                                         @AuthenticationPrincipal CustomUserDetails currentUser) {
        Optional<CourseEnrollment> enrollmentOptional = enrollmentRepository.findById(enrollmentId);
        if (enrollmentOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        CourseEnrollment enrollment = enrollmentOptional.get();
        
        // Verify student owns this enrollment
        if (!enrollment.getStudent().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).build();
        }

        enrollment.setCompletionPercentage(request.getCompletionPercentage());
        enrollment.setLastAccessed(java.time.LocalDateTime.now());
        
        CourseEnrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return ResponseEntity.ok(updatedEnrollment);
    }

    @GetMapping("/course/{courseId}/stats")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<EnrollmentStats> getCourseEnrollmentStats(@PathVariable Long courseId,
                                                                  @AuthenticationPrincipal CustomUserDetails currentUser) {
        // Verify instructor has permission for this course
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Course course = courseOptional.get();
        if (!course.getInstructor().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.status(403).build();
        }

        Long totalEnrollments = enrollmentRepository.countActiveEnrollmentsByCourse(courseId);
        Double averageCompletion = enrollmentRepository.getAverageCompletionByCourse(courseId);
        
        EnrollmentStats stats = new EnrollmentStats(totalEnrollments, averageCompletion);
        return ResponseEntity.ok(stats);
    }
}
