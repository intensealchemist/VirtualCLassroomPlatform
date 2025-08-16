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
@RequestMapping("/api/assignments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AssignmentController {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Assignment>> getAssignmentsByCourse(@PathVariable Long courseId,
                                                                 @RequestParam(defaultValue = "false") boolean publishedOnly) {
        List<Assignment> assignments;
        if (publishedOnly) {
            assignments = assignmentRepository.findByCourseIdAndIsPublishedTrue(courseId);
        } else {
            assignments = assignmentRepository.findByCourseId(courseId);
        }
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assignment> getAssignmentById(@PathVariable Long id) {
        Optional<Assignment> assignment = assignmentRepository.findById(id);
        return assignment.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<Assignment> createAssignment(@Valid @RequestBody Assignment assignment,
                                                     @AuthenticationPrincipal CustomUserDetails currentUser) {
        // Verify the course exists and user has permission
        Optional<Course> courseOptional = courseRepository.findById(assignment.getCourse().getId());
        if (courseOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Course course = courseOptional.get();
        if (!course.getInstructor().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.status(403).build();
        }

        assignment.setCourse(course);
        Assignment savedAssignment = assignmentRepository.save(assignment);
        return ResponseEntity.ok(savedAssignment);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<Assignment> updateAssignment(@PathVariable Long id,
                                                     @Valid @RequestBody Assignment assignmentDetails,
                                                     @AuthenticationPrincipal CustomUserDetails currentUser) {
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(id);
        if (assignmentOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Assignment assignment = assignmentOptional.get();
        
        // Check permissions
        if (!assignment.getCourse().getInstructor().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.status(403).build();
        }

        assignment.setTitle(assignmentDetails.getTitle());
        assignment.setDescription(assignmentDetails.getDescription());
        assignment.setDueDate(assignmentDetails.getDueDate());
        assignment.setMaxPoints(assignmentDetails.getMaxPoints());
        assignment.setAssignmentType(assignmentDetails.getAssignmentType());
        assignment.setPublished(assignmentDetails.isPublished());

        Assignment updatedAssignment = assignmentRepository.save(assignment);
        return ResponseEntity.ok(updatedAssignment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteAssignment(@PathVariable Long id,
                                            @AuthenticationPrincipal CustomUserDetails currentUser) {
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(id);
        if (assignmentOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Assignment assignment = assignmentOptional.get();
        
        // Check permissions
        if (!assignment.getCourse().getInstructor().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.status(403).build();
        }

        assignmentRepository.delete(assignment);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<Assignment> togglePublishStatus(@PathVariable Long id,
                                                        @AuthenticationPrincipal CustomUserDetails currentUser) {
        Optional<Assignment> assignmentOptional = assignmentRepository.findById(id);
        if (assignmentOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Assignment assignment = assignmentOptional.get();
        
        // Check permissions
        if (!assignment.getCourse().getInstructor().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.status(403).build();
        }

        assignment.setPublished(!assignment.isPublished());
        Assignment updatedAssignment = assignmentRepository.save(assignment);
        return ResponseEntity.ok(updatedAssignment);
    }

    @GetMapping("/instructor/my-assignments")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<Assignment>> getMyAssignments(@AuthenticationPrincipal CustomUserDetails currentUser) {
        List<Assignment> assignments = assignmentRepository.findByInstructorId(currentUser.getId());
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<Assignment>> getOverdueAssignments() {
        List<Assignment> assignments = assignmentRepository.findOverdueAssignments(LocalDateTime.now());
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/due-soon")
    public ResponseEntity<List<Assignment>> getAssignmentsDueSoon(@RequestParam(defaultValue = "7") int days) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(days);
        List<Assignment> assignments = assignmentRepository.findByDueDateBetween(startDate, endDate);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/by-type")
    public ResponseEntity<List<Assignment>> getAssignmentsByType(@RequestParam AssignmentType type) {
        List<Assignment> assignments = assignmentRepository.findByAssignmentType(type);
        return ResponseEntity.ok(assignments);
    }
}
