package com.virtualclassroom.controller;

import com.virtualclassroom.dto.FileUploadResponse;
import com.virtualclassroom.dto.LessonResourceDTO;
import com.virtualclassroom.mapper.LessonMapper;
import com.virtualclassroom.model.LessonResource;
import com.virtualclassroom.model.ResourceType;
import com.virtualclassroom.model.User;
import com.virtualclassroom.service.FileStorageService;
import com.virtualclassroom.service.LessonResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing lesson resources
 */
@RestController
@RequestMapping("/api/resources")
public class LessonResourceController {

    private final LessonResourceService lessonResourceService;
    private final FileStorageService fileStorageService;
    private final LessonMapper lessonMapper;

    @Autowired
    public LessonResourceController(LessonResourceService lessonResourceService,
                                    FileStorageService fileStorageService,
                                    LessonMapper lessonMapper) {
        this.lessonResourceService = lessonResourceService;
        this.fileStorageService = fileStorageService;
        this.lessonMapper = lessonMapper;
    }

    /**
     * Get all resources for a lesson
     * @param lessonId The lesson ID
     * @return List of lesson resource DTOs
     */
    @GetMapping("/lesson/{lessonId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<LessonResourceDTO>> getResourcesByLesson(@PathVariable Long lessonId) {
        List<LessonResource> resources = lessonResourceService.getResourcesByLesson(lessonId);
        if (resources.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        List<LessonResourceDTO> resourceDTOs = resources.stream()
                .map(lessonMapper::toResourceDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(resourceDTOs);
    }

    /**
     * Get a specific resource by ID
     * @param resourceId The resource ID
     * @return The lesson resource DTO
     */
    @GetMapping("/{resourceId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<LessonResourceDTO> getResourceById(@PathVariable Long resourceId) {
        LessonResource resource = lessonResourceService.getResourceById(resourceId);
        return ResponseEntity.ok(lessonMapper.toResourceDTO(resource));
    }

    /**
     * Create a new resource for a lesson
     * @param lessonId The lesson ID
     * @param name The resource name
     * @param description The resource description
     * @param type The resource type
     * @param file The resource file
     * @param user The authenticated user
     * @return The created resource DTO
     */
    @PostMapping("/lesson/{lessonId}/upload")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<LessonResourceDTO> uploadResource(
            @PathVariable Long lessonId,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("type") ResourceType type,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) {
        
        // Upload the file
        FileUploadResponse uploadResponse = fileStorageService.storeFile(file, "resources");
        
        // Create the resource
        LessonResource resource = lessonResourceService.createResource(
                lessonId,
                name,
                description,
                uploadResponse.getFileUrl(),
                type,
                file.getSize(),
                user
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonMapper.toResourceDTO(resource));
    }

    /**
     * Create a new external resource (link) for a lesson
     * @param lessonId The lesson ID
     * @param dto The resource DTO
     * @param user The authenticated user
     * @return The created resource DTO
     */
    @PostMapping("/lesson/{lessonId}/link")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<LessonResourceDTO> createExternalResource(
            @PathVariable Long lessonId,
            @RequestBody LessonResourceDTO dto,
            @AuthenticationPrincipal User user) {
        
        // Create the resource
        LessonResource resource = lessonResourceService.createResource(
                lessonId,
                dto.getTitle(),
                dto.getDescription(),
                dto.getResourceUrl(),
                ResourceType.LINK,
                null,
                user
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonMapper.toResourceDTO(resource));
    }

    /**
     * Update an existing resource
     * @param resourceId The resource ID
     * @param dto The updated resource DTO
     * @param user The authenticated user
     * @return The updated resource DTO
     */
    @PutMapping("/{resourceId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<LessonResourceDTO> updateResource(
            @PathVariable Long resourceId,
            @RequestBody LessonResourceDTO dto,
            @AuthenticationPrincipal User user) {
        
        // Get the resource type
        ResourceType type;
        try {
            type = ResourceType.valueOf(dto.getResourceType());
        } catch (IllegalArgumentException e) {
            type = ResourceType.OTHER;
        }
        
        // Update the resource
        LessonResource resource = lessonResourceService.updateResource(
                resourceId,
                dto.getTitle(),
                dto.getDescription(),
                dto.getResourceUrl(),
                type,
                null,
                user
        );
        
        return ResponseEntity.ok(lessonMapper.toResourceDTO(resource));
    }

    /**
     * Delete a resource
     * @param resourceId The resource ID
     * @param user The authenticated user
     * @return No content response
     */
    @DeleteMapping("/{resourceId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Void> deleteResource(
            @PathVariable Long resourceId,
            @AuthenticationPrincipal User user) {
        
        lessonResourceService.deleteResource(resourceId, user);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get resources by type for a lesson
     * @param lessonId The lesson ID
     * @param type The resource type
     * @return List of lesson resource DTOs
     */
    @GetMapping("/lesson/{lessonId}/type/{type}")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<LessonResourceDTO>> getResourcesByLessonAndType(
            @PathVariable Long lessonId,
            @PathVariable ResourceType type) {
        
        List<LessonResource> resources = lessonResourceService.getResourcesByLessonAndType(lessonId, type);
        if (resources.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        List<LessonResourceDTO> resourceDTOs = resources.stream()
                .map(lessonMapper::toResourceDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(resourceDTOs);
    }
}