package com.virtualclassroom.controller;

import com.virtualclassroom.dto.FileUploadResponse;
import com.virtualclassroom.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Controller for file upload and download operations
 */
@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Upload a profile picture
     * @param file The profile picture file
     * @return The file upload response
     */
    @PostMapping("/profile-picture")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FileUploadResponse> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeProfilePicture(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/")
                .path(fileName)
                .toUriString();

        FileUploadResponse response = new FileUploadResponse(
                fileName,
                fileDownloadUri,
                file.getContentType(),
                file.getSize());

        return ResponseEntity.ok().body(response);
    }

    /**
     * Upload a course resource
     * @param file The resource file
     * @return The file upload response
     */
    @PostMapping("/course-resource")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<FileUploadResponse> uploadCourseResource(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeCourseResource(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/")
                .path(fileName)
                .toUriString();

        FileUploadResponse response = new FileUploadResponse(
                fileName,
                fileDownloadUri,
                file.getContentType(),
                file.getSize());

        return ResponseEntity.ok().body(response);
    }

    /**
     * Upload an assignment file
     * @param file The assignment file
     * @return The file upload response
     */
    @PostMapping("/assignment")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<FileUploadResponse> uploadAssignment(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeAssignmentFile(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/")
                .path(fileName)
                .toUriString();

        FileUploadResponse response = new FileUploadResponse(
                fileName,
                fileDownloadUri,
                file.getContentType(),
                file.getSize());

        return ResponseEntity.ok().body(response);
    }

    /**
     * Upload a submission file
     * @param file The submission file
     * @return The file upload response
     */
    @PostMapping("/submission")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<FileUploadResponse> uploadSubmission(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeSubmissionFile(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/")
                .path(fileName)
                .toUriString();

        FileUploadResponse response = new FileUploadResponse(
                fileName,
                fileDownloadUri,
                file.getContentType(),
                file.getSize());

        return ResponseEntity.ok().body(response);
    }

    /**
     * Upload a video file
     * @param file The video file
     * @return The file upload response
     */
    @PostMapping("/video")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<FileUploadResponse> uploadVideo(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeVideoFile(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/")
                .path(fileName)
                .toUriString();

        FileUploadResponse response = new FileUploadResponse(
                fileName,
                fileDownloadUri,
                file.getContentType(),
                file.getSize());

        return ResponseEntity.ok().body(response);
    }

    /**
     * Download a file
     * @param fileName The file name
     * @param request The HTTP request
     * @return The file resource
     */
    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Default content type if type could not be determined
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * Delete a file
     * @param fileName The file name
     * @return The response entity
     */
    @DeleteMapping("/{fileName:.+}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileName) {
        fileStorageService.deleteFile(fileName);
        return ResponseEntity.ok().build();
    }
}