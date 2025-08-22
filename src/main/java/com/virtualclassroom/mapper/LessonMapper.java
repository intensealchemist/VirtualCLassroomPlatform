package com.virtualclassroom.mapper;

import com.virtualclassroom.dto.LessonDTO;
import com.virtualclassroom.dto.LessonResourceDTO;
import com.virtualclassroom.model.Lesson;
import com.virtualclassroom.model.LessonResource;
import com.virtualclassroom.model.ResourceType;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Lesson entities and DTOs
 */
@Component
public class LessonMapper {

    /**
     * Convert a Lesson entity to a LessonDTO
     * @param lesson The Lesson entity
     * @return The LessonDTO
     */
    public LessonDTO toDTO(Lesson lesson) {
        if (lesson == null) {
            return null;
        }
        
        LessonDTO dto = new LessonDTO();
        dto.setId(lesson.getId());
        dto.setTitle(lesson.getTitle());
        dto.setDescription(lesson.getDescription());
        dto.setContent(lesson.getContent());
        
        if (lesson.getCourse() != null) {
            dto.setCourseId(lesson.getCourse().getId());
            dto.setCourseName(lesson.getCourse().getTitle());
        }
        
        dto.setOrderIndex(lesson.getLessonOrder());
        dto.setVideoUrl(lesson.getVideoUrl());
        dto.setAttachmentUrl(lesson.getDocumentUrl());
        dto.setCreatedAt(lesson.getCreatedAt());
        dto.setUpdatedAt(lesson.getUpdatedAt());
        dto.setPublished(Boolean.TRUE.equals(lesson.getIsPublished()));
        
        if (lesson.getResources() != null) {
            dto.setResources(lesson.getResources().stream()
                    .map(this::toResourceDTO)
                    .collect(Collectors.toSet()));
        }
        
        return dto;
    }
    
    /**
     * Convert a LessonResource entity to a LessonResourceDTO
     * @param resource The LessonResource entity
     * @return The LessonResourceDTO
     */
    public LessonResourceDTO toResourceDTO(LessonResource resource) {
        if (resource == null) {
            return null;
        }
        
        LessonResourceDTO dto = new LessonResourceDTO();
        dto.setId(resource.getId());
        dto.setTitle(resource.getTitle());
        dto.setDescription(resource.getDescription());
        dto.setResourceUrl(resource.getResourceUrl());
        dto.setResourceType(resource.getResourceType() != null ? resource.getResourceType().name() : null);
        
        if (resource.getLesson() != null) {
            dto.setLessonId(resource.getLesson().getId());
        }
        
        dto.setCreatedAt(resource.getCreatedAt());
        dto.setUpdatedAt(resource.getUpdatedAt());
        
        return dto;
    }
    
    /**
     * Convert a LessonDTO to a Lesson entity
     * @param dto The LessonDTO
     * @return The Lesson entity
     */
    public Lesson toEntity(LessonDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Lesson lesson = new Lesson();
        updateEntityFromDTO(lesson, dto);
        return lesson;
    }
    
    /**
     * Update a Lesson entity from a LessonDTO
     * @param lesson The Lesson entity to update
     * @param dto The LessonDTO with updated values
     */
    public void updateEntityFromDTO(Lesson lesson, LessonDTO dto) {
        if (dto == null) {
            return;
        }
        
        lesson.setTitle(dto.getTitle());
        lesson.setDescription(dto.getDescription());
        lesson.setContent(dto.getContent());
        lesson.setLessonOrder(dto.getOrderIndex());
        lesson.setVideoUrl(dto.getVideoUrl());
        lesson.setDocumentUrl(dto.getAttachmentUrl());
        lesson.setIsPublished(dto.isPublished());
        
        // Note: Course relationship should be set separately
        // Note: Resources should be managed separately
    }
    
    /**
     * Convert a LessonResourceDTO to a LessonResource entity
     * @param dto The LessonResourceDTO
     * @return The LessonResource entity
     */
    public LessonResource toResourceEntity(LessonResourceDTO dto) {
        if (dto == null) {
            return null;
        }
        
        LessonResource resource = new LessonResource();
        updateResourceEntityFromDTO(resource, dto);
        return resource;
    }
    
    /**
     * Update a LessonResource entity from a LessonResourceDTO
     * @param resource The LessonResource entity to update
     * @param dto The LessonResourceDTO with updated values
     */
    public void updateResourceEntityFromDTO(LessonResource resource, LessonResourceDTO dto) {
        if (dto == null) {
            return;
        }
        
        resource.setTitle(dto.getTitle());
        resource.setDescription(dto.getDescription());
        resource.setResourceUrl(dto.getResourceUrl());
        resource.setResourceType(dto.getResourceType() != null ? ResourceType.valueOf(dto.getResourceType()) : null);
        
        // Note: Lesson relationship should be set separately
    }
    
    /**
     * Convert a set of Lesson entities to a set of LessonDTOs
     * @param lessons The set of Lesson entities
     * @return The set of LessonDTOs
     */
    public Set<LessonDTO> toDTOSet(Set<Lesson> lessons) {
        if (lessons == null) {
            return new HashSet<>();
        }
        
        return lessons.stream()
                .map(this::toDTO)
                .collect(Collectors.toSet());
    }
    
    /**
     * Convert a set of LessonDTOs to a set of Lesson entities
     * @param dtos The set of LessonDTOs
     * @return The set of Lesson entities
     */
    public Set<Lesson> toEntitySet(Set<LessonDTO> dtos) {
        if (dtos == null) {
            return new HashSet<>();
        }
        
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toSet());
    }
}