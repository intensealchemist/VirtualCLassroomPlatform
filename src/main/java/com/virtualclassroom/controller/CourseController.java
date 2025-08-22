package com.virtualclassroom.controller;

import com.virtualclassroom.dto.CourseDTO;
import com.virtualclassroom.dto.EnrollmentRequest;
import com.virtualclassroom.dto.FileUploadResponse;
import com.virtualclassroom.mapper.CourseMapper;
import com.virtualclassroom.model.User;
import com.virtualclassroom.service.ICourseService;
import com.virtualclassroom.service.FileStorageService;
import com.virtualclassroom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Controller for course-related operations
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private ICourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private CourseMapper courseMapper;

    /**
     * Get all public courses
     * @param pageable The pagination information
     * @return The page of courses
     */
    @GetMapping
    public ResponseEntity<Page<CourseDTO>> getAllPublicCourses(Pageable pageable) {
        Page<CourseDTO> courses = courseService.getAllPublicCourses(pageable);
        return ResponseEntity.ok(courses);
    }

    /**
     * Get a course by ID
     * @param id The course ID
     * @return The course
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        CourseDTO course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    /**
     * Create a new course
     * @param courseDTO The course data
     * @param user The authenticated user
     * @return The created course
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<CourseDTO> createCourse(
            @Valid @RequestBody CourseDTO courseDTO,
            @AuthenticationPrincipal User user) {
        CourseDTO createdCourse = courseService.createCourse(courseDTO, user);
        return new ResponseEntity<>(createdCourse, HttpStatus.CREATED);
    }

    /**
     * Update a course
     * @param id The course ID
     * @param courseDTO The updated course data
     * @param user The authenticated user
     * @return The updated course
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<CourseDTO> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseDTO courseDTO,
            @AuthenticationPrincipal User user) {
        CourseDTO updatedCourse = courseService.updateCourse(id, courseDTO, user);
        return ResponseEntity.ok(updatedCourse);
    }

    /**
     * Delete a course
     * @param id The course ID
     * @param user The authenticated user
     * @return No content
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        courseService.deleteCourse(id, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * Upload a course cover image
     * @param id The course ID
     * @param file The cover image file
     * @param user The authenticated user
     * @return The file upload response
     */
    @PostMapping("/{id}/cover-image")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<FileUploadResponse> uploadCoverImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) {
        CourseDTO course = courseService.uploadCoverImage(id, file, user);
        String downloadUri = "/api/files/" + course.getCoverImage();
        FileUploadResponse response = new FileUploadResponse(
                course.getCoverImage(),
                downloadUri,
                file.getContentType(),
                file.getSize());
        return ResponseEntity.ok(response);
    }

    /**
     * Enroll a student in a course
     * @param id The course ID
     * @param enrollmentRequest The enrollment request
     * @param user The authenticated user
     * @return No content
     */
    @PostMapping("/{id}/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> enrollStudent(
            @PathVariable Long id,
            @RequestBody EnrollmentRequest enrollmentRequest,
            @AuthenticationPrincipal User user) {
        courseService.enrollStudent(id, user, enrollmentRequest.getEnrollmentKey());
        return ResponseEntity.ok().build();
    }

    /**
     * Unenroll a student from a course
     * @param id The course ID
     * @param user The authenticated user
     * @return No content
     */
    @PostMapping("/{id}/unenroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> unenrollStudent(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        courseService.unenrollStudent(id, user);
        return ResponseEntity.ok().build();
    }

    /**
     * Get courses taught by an instructor
     * @param user The authenticated user
     * @return The list of courses
     */
    @GetMapping("/teaching")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<CourseDTO>> getCoursesByInstructor(
            @AuthenticationPrincipal User user) {
        List<CourseDTO> courses = courseService.getCoursesByInstructor(user);
        return ResponseEntity.ok(courses);
    }

    /**
     * Get courses enrolled by a student
     * @param user The authenticated user
     * @return The list of courses
     */
    @GetMapping("/enrolled")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<CourseDTO>> getEnrolledCourses(
            @AuthenticationPrincipal User user) {
        List<CourseDTO> courses = courseService.getEnrolledCourses(user);
        return ResponseEntity.ok(courses);
    }
}