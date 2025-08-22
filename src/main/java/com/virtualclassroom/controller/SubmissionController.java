package com.virtualclassroom.controller;

import com.virtualclassroom.dto.FileUploadResponse;
import com.virtualclassroom.model.Submission;
import com.virtualclassroom.model.User;
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
 * Controller for submission-related operations
 */
@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Get all submissions for a student
     * @param user The authenticated user
     * @return The list of submissions
     */
    @GetMapping("/my-submissions")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<Submission>> getMySubmissions(@AuthenticationPrincipal User user) {
        List<Submission> submissions = submissionService.getSubmissionsByStudent(user);
        return ResponseEntity.ok(submissions);
    }

    /**
     * Get a specific submission by ID
     * @param submissionId The submission ID
     * @param user The authenticated user
     * @return The submission
     */
    @GetMapping("/{submissionId}")
    public ResponseEntity<Submission> getSubmissionById(
            @PathVariable Long submissionId,
            @AuthenticationPrincipal User user) {
        Submission submission = submissionService.getSubmissionById(submissionId, user);
        return ResponseEntity.ok(submission);
    }

    /**
     * Update a submission
     * @param submissionId The submission ID
     * @param submission The updated submission data
     * @param user The authenticated user
     * @return The updated submission
     */
    @PutMapping("/{submissionId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Submission> updateSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody Submission submission,
            @AuthenticationPrincipal User user) {
        submission.setId(submissionId);
        Submission updatedSubmission = submissionService.updateSubmission(submission, user);
        return ResponseEntity.ok(updatedSubmission);
    }

    /**
     * Upload a file for a submission
     * @param submissionId The submission ID
     * @param file The submission file
     * @param user The authenticated user
     * @return The file upload response
     */
    @PostMapping("/{submissionId}/upload")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<FileUploadResponse> uploadSubmissionFile(
            @PathVariable Long submissionId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) {
        String submissionUrl = fileStorageService.storeSubmissionFile(file);
        Submission submission = submissionService.updateSubmissionFile(submissionId, submissionUrl, user);
        
        String downloadUri = "/api/files/" + submissionUrl;
        FileUploadResponse response = new FileUploadResponse(
                submissionUrl,
                downloadUri,
                file.getContentType(),
                file.getSize());
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a submission
     * @param submissionId The submission ID
     * @param user The authenticated user
     * @return No content
     */
    @DeleteMapping("/{submissionId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> deleteSubmission(
            @PathVariable Long submissionId,
            @AuthenticationPrincipal User user) {
        submissionService.deleteSubmission(submissionId, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all submissions for an instructor
     * @param user The authenticated user
     * @return The list of submissions
     */
    @GetMapping("/instructor-submissions")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<Submission>> getInstructorSubmissions(@AuthenticationPrincipal User user) {
        List<Submission> submissions = submissionService.getSubmissionsForInstructor(user);
        return ResponseEntity.ok(submissions);
    }

    /**
     * Grade a submission
     * @param submissionId The submission ID
     * @param submission The updated submission with grade and feedback
     * @param user The authenticated user
     * @return The updated submission
     */
    @PutMapping("/{submissionId}/grade")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Submission> gradeSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody Submission submission,
            @AuthenticationPrincipal User user) {
        submission.setId(submissionId);
        Submission gradedSubmission = submissionService.gradeSubmission(submissionId, submission, user);
        return ResponseEntity.ok(gradedSubmission);
    }

    /**
     * Get all ungraded submissions for an instructor
     * @param user The authenticated user
     * @return The list of ungraded submissions
     */
    @GetMapping("/ungraded")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<Submission>> getUngradedSubmissions(@AuthenticationPrincipal User user) {
        List<Submission> submissions = submissionService.getUngradedSubmissionsForInstructor(user);
        return ResponseEntity.ok(submissions);
    }

    /**
     * Get submission statistics for a course
     * @param courseId The course ID
     * @param user The authenticated user
     * @return The submission statistics
     */
    @GetMapping("/stats/course/{courseId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Object> getCourseSubmissionStats(
            @PathVariable Long courseId,
            @AuthenticationPrincipal User user) {
        Object stats = submissionService.getCourseSubmissionStatistics(courseId, user);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get submission statistics for a student
     * @param user The authenticated user
     * @return The submission statistics
     */
    @GetMapping("/stats/my-submissions")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Object> getMySubmissionStats(@AuthenticationPrincipal User user) {
        Object stats = submissionService.getStudentSubmissionStatistics(user);
        return ResponseEntity.ok(stats);
    }
}