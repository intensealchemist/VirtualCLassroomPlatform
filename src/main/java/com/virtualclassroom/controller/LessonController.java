package com.virtualclassroom.controller;

import com.virtualclassroom.dto.FileUploadResponse;
import com.virtualclassroom.dto.LessonDTO;
import com.virtualclassroom.mapper.LessonMapper;
import com.virtualclassroom.model.Lesson;
import com.virtualclassroom.model.User;
import com.virtualclassroom.service.FileStorageService;
import com.virtualclassroom.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for lesson-related operations
 */
@RestController
@RequestMapping("/api/courses/{courseId}/lessons")
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private LessonMapper lessonMapper;

    /**
     * Get all lessons for a course
     * @param courseId The course ID
     * @return The list of lessons as DTOs
     */
    @GetMapping
    public ResponseEntity<List<LessonDTO>> getLessonsByCourse(@PathVariable Long courseId) {
        List<Lesson> lessons = lessonService.getLessonsByCourse(courseId);
        List<LessonDTO> lessonDTOs = lessons.stream()
                .map(lessonMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lessonDTOs);
    }

    /**
     * Get a lesson by ID
     * @param courseId The course ID
     * @param lessonId The lesson ID
     * @return The lesson as DTO
     */
    @GetMapping("/{lessonId}")
    public ResponseEntity<LessonDTO> getLessonById(
            @PathVariable Long courseId,
            @PathVariable Long lessonId) {
        Lesson lesson = lessonService.getLessonById(lessonId, courseId);
        LessonDTO lessonDTO = lessonMapper.toDTO(lesson);
        return ResponseEntity.ok(lessonDTO);
    }

    /**
     * Create a new lesson
     * @param courseId The course ID
     * @param lessonDTO The lesson data as DTO
     * @param user The authenticated user
     * @return The created lesson as DTO
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<LessonDTO> createLesson(
            @PathVariable Long courseId,
            @Valid @RequestBody LessonDTO lessonDTO,
            @AuthenticationPrincipal User user) {
        Lesson lesson = lessonMapper.toEntity(lessonDTO);
        Lesson createdLesson = lessonService.createLesson(courseId, lesson, user);
        LessonDTO createdLessonDTO = lessonMapper.toDTO(createdLesson);
        return new ResponseEntity<>(createdLessonDTO, HttpStatus.CREATED);
    }

    /**
     * Update a lesson
     * @param courseId The course ID
     * @param lessonId The lesson ID
     * @param lessonDTO The updated lesson data as DTO
     * @param user The authenticated user
     * @return The updated lesson as DTO
     */
    @PutMapping("/{lessonId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<LessonDTO> updateLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonDTO lessonDTO,
            @AuthenticationPrincipal User user) {
        lessonDTO.setId(lessonId);
        Lesson lesson = lessonMapper.toEntity(lessonDTO);
        Lesson updatedLesson = lessonService.updateLesson(courseId, lesson, user);
        LessonDTO updatedLessonDTO = lessonMapper.toDTO(updatedLesson);
        return ResponseEntity.ok(updatedLessonDTO);
    }

    /**
     * Delete a lesson
     * @param courseId The course ID
     * @param lessonId The lesson ID
     * @param user The authenticated user
     * @return No content
     */
    @DeleteMapping("/{lessonId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Void> deleteLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @AuthenticationPrincipal User user) {
        lessonService.deleteLesson(lessonId, courseId, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * Upload a video for a lesson
     * @param courseId The course ID
     * @param lessonId The lesson ID
     * @param file The video file
     * @param user The authenticated user
     * @return The file upload response
     */
    @PostMapping("/{lessonId}/video")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<FileUploadResponse> uploadVideo(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) {
        String videoUrl = fileStorageService.storeVideoFile(file);
        Lesson updatedLesson = lessonService.updateLessonVideo(lessonId, courseId, videoUrl, user);
        
        String downloadUri = "/api/files/" + videoUrl;
        FileUploadResponse response = new FileUploadResponse(
                videoUrl,
                downloadUri,
                file.getContentType(),
                file.getSize());
        return ResponseEntity.ok(response);
    }

    /**
     * Upload an attachment for a lesson
     * @param courseId The course ID
     * @param lessonId The lesson ID
     * @param file The attachment file
     * @param user The authenticated user
     * @return The file upload response
     */
    @PostMapping("/{lessonId}/attachment")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<FileUploadResponse> uploadAttachment(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) {
        String attachmentUrl = fileStorageService.storeCourseResource(file);
        Lesson updatedLesson = lessonService.updateLessonAttachment(lessonId, courseId, attachmentUrl, user);
        
        String downloadUri = "/api/files/" + attachmentUrl;
        FileUploadResponse response = new FileUploadResponse(
                attachmentUrl,
                downloadUri,
                file.getContentType(),
                file.getSize());
        return ResponseEntity.ok(response);
    }

    /**
     * Reorder lessons in a course
     * @param courseId The course ID
     * @param lessonIds The ordered list of lesson IDs
     * @param user The authenticated user
     * @return No content
     */
    @PostMapping("/reorder")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<LessonDTO>> reorderLessons(
            @PathVariable Long courseId,
            @RequestBody List<Long> lessonIds,
            @AuthenticationPrincipal User user) {
        List<Lesson> reorderedLessons = lessonService.reorderLessons(courseId, lessonIds, user);
        List<LessonDTO> lessonDTOs = reorderedLessons.stream()
                .map(lessonMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lessonDTOs);
    }

    /**
     * Reorder a lesson
     * @param courseId The course ID
     * @param lessonId The lesson ID
     * @param newOrder The new order
     * @param user The authenticated user
     * @return The reordered lesson as DTO
     */
    @PutMapping("/{lessonId}/reorder")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<LessonDTO> reorderLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RequestParam int newOrder,
            @AuthenticationPrincipal User user) {
        Lesson reorderedLesson = lessonService.reorderLesson(lessonId, courseId, newOrder, user);
        LessonDTO reorderedLessonDTO = lessonMapper.toDTO(reorderedLesson);
        return ResponseEntity.ok(reorderedLessonDTO);
    }

    /**
     * Publish or unpublish a lesson
     * @param courseId The course ID
     * @param lessonId The lesson ID
     * @param publish Whether to publish or unpublish
     * @param user The authenticated user
     * @return The updated lesson as DTO
     */
    @PutMapping("/{lessonId}/publish")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<LessonDTO> publishLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RequestParam boolean publish,
            @AuthenticationPrincipal User user) {
        Lesson updatedLesson = lessonService.publishLesson(lessonId, courseId, publish, user);
        LessonDTO updatedLessonDTO = lessonMapper.toDTO(updatedLesson);
        return ResponseEntity.ok(updatedLessonDTO);
    }
}