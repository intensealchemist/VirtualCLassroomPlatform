package com.virtualclassroom.service;

import com.virtualclassroom.config.FileStorageConfig;
import com.virtualclassroom.dto.FileUploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * Service for handling file operations
 */
@Service
public class FileStorageService {

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/png", "image/gif", "image/svg+xml"
    );
    
    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
        "application/pdf", "application/msword", 
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/vnd.ms-powerpoint",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "text/plain", "text/csv"
    );
    
    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
        "video/mp4", "video/webm", "video/ogg"
    );
    
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB
    
    @Autowired
    private FileStorageConfig fileStorageConfig;
    
    /**
     * Store a profile picture
     * @param file The profile picture file
     * @return The stored file path
     */
    public String storeProfilePicture(MultipartFile file) {
        validateFileType(file, ALLOWED_IMAGE_TYPES);
        validateFileSize(file);
        return fileStorageConfig.store(file, "profiles");
    }
    
    /**
     * Store a file in the specified directory
     * @param file The file to store
     * @param directory The directory to store the file in
     * @return The file upload response
     */
    public FileUploadResponse storeFile(MultipartFile file, String directory) {
        // Validate file size for all files
        validateFileSize(file);
        
        // Store the file
        String filePath = fileStorageConfig.store(file, directory);
        
        // Create and return the response
        return new FileUploadResponse(
            file.getOriginalFilename(),
            filePath,
            file.getContentType(),
            file.getSize()
        );
    }
    
    /**
     * Store a course resource file
     * @param file The resource file
     * @return The stored file path
     */
    public String storeCourseResource(MultipartFile file) {
        validateFileType(file, ALLOWED_DOCUMENT_TYPES);
        validateFileSize(file);
        return fileStorageConfig.store(file, "resources");
    }
    
    /**
     * Store an assignment file
     * @param file The assignment file
     * @return The stored file path
     */
    public String storeAssignmentFile(MultipartFile file) {
        validateFileType(file, ALLOWED_DOCUMENT_TYPES);
        validateFileSize(file);
        return fileStorageConfig.store(file, "assignments");
    }
    
    /**
     * Store a submission file
     * @param file The submission file
     * @return The stored file path
     */
    public String storeSubmissionFile(MultipartFile file) {
        validateFileType(file, ALLOWED_DOCUMENT_TYPES);
        validateFileSize(file);
        return fileStorageConfig.store(file, "submissions");
    }
    
    /**
     * Store a video file
     * @param file The video file
     * @return The stored file path
     */
    public String storeVideoFile(MultipartFile file) {
        validateFileType(file, ALLOWED_VIDEO_TYPES);
        validateFileSize(file);
        return fileStorageConfig.store(file, "videos");
    }
    
    /**
     * Load a file as a resource
     * @param filename The file path
     * @return The file resource
     */
    public Resource loadFileAsResource(String filename) {
        return fileStorageConfig.loadAsResource(filename);
    }
    
    /**
     * Delete a file
     * @param filename The file path
     */
    public void deleteFile(String filename) {
        fileStorageConfig.deleteFile(filename);
    }
    
    /**
     * Validate the file type
     * @param file The file to validate
     * @param allowedTypes The allowed file types
     */
    private void validateFileType(MultipartFile file, List<String> allowedTypes) {
        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new IllegalArgumentException("File type not allowed. Allowed types: " + allowedTypes);
        }
    }
    
    /**
     * Validate the file size
     * @param file The file to validate
     */
    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum limit of " + (MAX_FILE_SIZE / (1024 * 1024)) + "MB");
        }
    }
}