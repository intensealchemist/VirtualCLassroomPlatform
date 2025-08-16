package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FileUploadController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
        "pdf", "doc", "docx", "txt", "jpg", "jpeg", "png", "gif", "mp4", "avi", "mov"
    );

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

    @PostMapping("/upload")
    @PreAuthorize("hasRole('STUDENT') or hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                      @RequestParam(value = "assignmentId", required = false) Long assignmentId,
                                      @RequestParam(value = "courseId", required = false) Long courseId,
                                      @AuthenticationPrincipal CustomUserDetails currentUser) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.badRequest().body("File size exceeds maximum limit of 50MB");
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return ResponseEntity.badRequest().body("Invalid filename");
            }

            String fileExtension = getFileExtension(originalFilename);
            if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
                return ResponseEntity.badRequest().body("File type not allowed");
            }

            // Create unique filename
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String uniqueFilename = String.format("%s_%s_%s.%s", 
                currentUser.getId(), timestamp, UUID.randomUUID().toString().substring(0, 8), fileExtension);

            // Create directory structure
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Save file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Create file metadata
            FileMetadata fileMetadata = new FileMetadata(
                originalFilename,
                uniqueFilename,
                file.getSize(),
                file.getContentType(),
                currentUser.getUser(),
                assignmentId,
                courseId
            );

            FileUploadResponse response = new FileUploadResponse(
                fileMetadata.getOriginalName(),
                fileMetadata.getStoredName(),
                fileMetadata.getFileSize(),
                "/api/files/download/" + uniqueFilename
            );

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<?> downloadFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent = Files.readAllBytes(filePath);
            String contentType = Files.probeContentType(filePath);
            
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(fileContent);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("File download failed");
        }
    }

    @DeleteMapping("/delete/{filename}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteFile(@PathVariable String filename,
                                      @AuthenticationPrincipal CustomUserDetails currentUser) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            Files.delete(filePath);
            return ResponseEntity.ok("File deleted successfully");

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("File deletion failed");
        }
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<String>> listFiles() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                return ResponseEntity.ok(Arrays.asList());
            }

            List<String> files = Files.list(uploadPath)
                .filter(Files::isRegularFile)
                .map(path -> path.getFileName().toString())
                .toList();

            return ResponseEntity.ok(files);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
}
