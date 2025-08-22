package com.virtualclassroom.controller;

import com.virtualclassroom.dto.FileUploadResponse;
import com.virtualclassroom.model.Assignment;
import com.virtualclassroom.model.Submission;
import com.virtualclassroom.model.User;
import com.virtualclassroom.service.AssignmentService;
import com.virtualclassroom.service.FileStorageService;
import com.virtualclassroom.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller for assignment-related operations
 */
@RestController
@RequestMapping("/api/courses/{courseId}/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Get all assignments for a course
     * @param courseId The course ID
     * @return The list of assignments
     */
    @GetMapping
    public ResponseEntity<List<Assignment>> getAssignmentsByCourse(@PathVariable Long courseId) {
        List<Assignment> assignments = assignmentService.getAssignmentsByCourse(courseId);
        return ResponseEntity.ok(assignments);
    }

    /**
     * Get an assignment by ID
     * @param courseId The course ID
     * @param assignmentId The assignment ID
     * @return The assignment
     */
    @GetMapping("/{assignmentId}")
    public ResponseEntity<Assignment> getAssignmentById(
            @PathVariable Long courseId,
            @PathVariable Long assignmentId) {
        Assignment assignment = assignmentService.getAssignmentById(assignmentId, courseId);
        return ResponseEntity.ok(assignment);
    }

    /**
     * Create a new assignment
     * @param courseId The course ID
     * @param assignment The assignment data
     * @param user The authenticated user
     * @return The created assignment
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Assignment> createAssignment(
            @PathVariable Long courseId,
            @Valid @RequestBody Assignment assignment,
            @AuthenticationPrincipal User user) {
        Assignment createdAssignment = assignmentService.createAssignment(courseId, assignment, user);
        return new ResponseEntity<>(createdAssignment, HttpStatus.CREATED);
    }

    /**
     * Update an assignment
     * @param courseId The course ID
     * @param assignmentId The assignment ID
     * @param assignment The updated assignment data
     * @param user The authenticated user
     * @return The updated assignment
     */
    @PutMapping("/{assignmentId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Assignment> updateAssignment(
            @PathVariable Long courseId,
            @PathVariable Long assignmentId,
            @Valid @RequestBody Assignment assignment,
            @AuthenticationPrincipal User user) {
        assignment.setId(assignmentId);
        Assignment updatedAssignment = assignmentService.updateAssignment(courseId, assignment, user);
        return ResponseEntity.ok(updatedAssignment);
    }

    /**
     * Delete an assignment
     * @param courseId The course ID
     * @param assignmentId The assignment ID
     * @param user The authenticated user
     * @return No content
     */
    @DeleteMapping("/{assignmentId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Void> deleteAssignment(
            @PathVariable Long courseId,
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal User user) {
        assignmentService.deleteAssignment(assignmentId, courseId, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * Upload an attachment for an assignment
     * @param courseId The course ID
     * @param assignmentId The assignment ID
     * @param file The attachment file
     * @param user The authenticated user
     * @return The file upload response
     */
    @PostMapping("/{assignmentId}/attachment")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<FileUploadResponse> uploadAttachment(
            @PathVariable Long courseId,
            @PathVariable Long assignmentId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) {
        String attachmentUrl = fileStorageService.storeAssignmentFile(file);
        assignmentService.updateAssignmentAttachment(assignmentId, courseId, attachmentUrl, user);
        
        String downloadUri = "/api/files/" + attachmentUrl;
        FileUploadResponse response = new FileUploadResponse(
                attachmentUrl,
                downloadUri,
                file.getContentType(),
                file.getSize());
        return ResponseEntity.ok(response);
    }

    /**
     * Publish or unpublish an assignment
     * @param courseId The course ID
     * @param assignmentId The assignment ID
     * @param publish Whether to publish or unpublish
     * @param user The authenticated user
     * @return The updated assignment
     */
    @PutMapping("/{assignmentId}/publish")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Assignment> publishAssignment(
            @PathVariable Long courseId,
            @PathVariable Long assignmentId,
            @RequestParam boolean publish,
            @AuthenticationPrincipal User user) {
        Assignment updatedAssignment = assignmentService.publishAssignment(assignmentId, courseId, publish, user);
        return ResponseEntity.ok(updatedAssignment);
    }

    /**
     * Get all submissions for an assignment
     * @param courseId The course ID
     * @param assignmentId The assignment ID
     * @param user The authenticated user
     * @return The list of submissions
     */
    @GetMapping("/{assignmentId}/submissions")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<Submission>> getSubmissionsByAssignment(
            @PathVariable Long courseId,
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal User user) {
        List<Submission> submissions = submissionService.getSubmissionsByAssignment(assignmentId, courseId, user);
        return ResponseEntity.ok(submissions);
    }

    /**
     * Get a student's submission for an assignment
     * @param courseId The course ID
     * @param assignmentId The assignment ID
     * @param user The authenticated user
     * @return The submission
     */
    @GetMapping("/{assignmentId}/my-submission")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Submission> getMySubmission(
            @PathVariable Long courseId,
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal User user) {
        Submission submission = submissionService.getStudentSubmission(assignmentId, user);
        return ResponseEntity.ok(submission);
    }

    /**
     * Submit an assignment
     * @param courseId The course ID
     * @param assignmentId The assignment ID
     * @param submission The submission data
     * @param user The authenticated user
     * @return The created submission
     */
    @PostMapping("/{assignmentId}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Submission> submitAssignment(
            @PathVariable Long courseId,
            @PathVariable Long assignmentId,
            @Valid @RequestBody Submission submission,
            @AuthenticationPrincipal User user) {
        Submission createdSubmission = submissionService.createSubmission(assignmentId, submission, user);
        return new ResponseEntity<>(createdSubmission, HttpStatus.CREATED);
    }

    /**
     * Upload a file for a submission
     * @param courseId The course ID
     * @param assignmentId The assignment ID
     * @param file The submission file
     * @param user The authenticated user
     * @return The file upload response
     */
    @PostMapping("/{assignmentId}/submit-file")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<FileUploadResponse> uploadSubmissionFile(
            @PathVariable Long courseId,
            @PathVariable Long assignmentId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) {
        String submissionUrl = fileStorageService.storeSubmissionFile(file);
        Submission submission = submissionService.createOrUpdateSubmissionWithFile(assignmentId, submissionUrl, user);
        
        String downloadUri = "/api/files/" + submissionUrl;
        FileUploadResponse response = new FileUploadResponse(
                submissionUrl,
                downloadUri,
                file.getContentType(),
                file.getSize());
        return ResponseEntity.ok(response);
    }

    /**
     * Grade a submission
     * @param courseId The course ID
     * @param assignmentId The assignment ID
     * @param submissionId The submission ID
     * @param submission The updated submission with grade and feedback
     * @param user The authenticated user
     * @return The updated submission
     */
    @PutMapping("/{assignmentId}/submissions/{submissionId}/grade")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Submission> gradeSubmission(
            @PathVariable Long courseId,
            @PathVariable Long assignmentId,
            @PathVariable Long submissionId,
            @Valid @RequestBody Submission submission,
            @AuthenticationPrincipal User user) {
        submission.setId(submissionId);
        Submission gradedSubmission = submissionService.gradeSubmission(submissionId, assignmentId, submission, user);
        return ResponseEntity.ok(gradedSubmission);
    }
}