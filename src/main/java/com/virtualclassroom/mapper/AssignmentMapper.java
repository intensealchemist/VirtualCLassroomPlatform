 package com.virtualclassroom.mapper;
 
 import com.virtualclassroom.dto.AssignmentDTO;
 import com.virtualclassroom.model.Assignment;
 import com.virtualclassroom.model.AssignmentAttachment;
 import org.springframework.stereotype.Component;
 
 import java.math.BigDecimal;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 import java.util.stream.Collectors;

/**
 * Mapper for converting between Assignment entities and DTOs
 */
@Component
public class AssignmentMapper {

    /**
     * Convert an Assignment entity to an AssignmentDTO
     * @param assignment The Assignment entity
     * @return The AssignmentDTO
     */
    public AssignmentDTO toDTO(Assignment assignment) {
        if (assignment == null) {
            return null;
        }
        
        AssignmentDTO dto = new AssignmentDTO();
        dto.setId(assignment.getId());
        dto.setTitle(assignment.getTitle());
        dto.setDescription(assignment.getDescription());
        dto.setInstructions(assignment.getInstructions());
        
        if (assignment.getCourse() != null) {
            dto.setCourseId(assignment.getCourse().getId());
            dto.setCourseName(assignment.getCourse().getTitle());
        }
        
        dto.setDueDate(assignment.getDueDate());
        if (assignment.getMaxPoints() != null) {
            dto.setMaxPoints(assignment.getMaxPoints().intValue());
        }
         // Map first attachment URL (DTO currently supports a single attachmentUrl)
         if (assignment.getAttachments() != null && !assignment.getAttachments().isEmpty()) {
             String firstUrl = assignment.getAttachments().stream()
                     .map(AssignmentAttachment::getUrl)
                     .filter(u -> u != null && !u.isBlank())
                     .findFirst()
                     .orElse(null);
             dto.setAttachmentUrl(firstUrl);
         }
        dto.setCreatedAt(assignment.getCreatedAt());
        dto.setUpdatedAt(assignment.getUpdatedAt());
         dto.setPublished(Boolean.TRUE.equals(assignment.getIsPublished()));
        
        if (assignment.getSubmissions() != null) {
            dto.setSubmissionCount(assignment.getSubmissions().size());
            dto.setGradedCount((int) assignment.getSubmissions().stream()
                    .filter(s -> s.getGrade() != null)
                    .count());
        }
        
        return dto;
    }
    
    /**
     * Convert an AssignmentDTO to an Assignment entity
     * @param dto The AssignmentDTO
     * @return The Assignment entity
     */
    public Assignment toEntity(AssignmentDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Assignment assignment = new Assignment();
        updateEntityFromDTO(assignment, dto);
        return assignment;
    }
    
    /**
     * Update an Assignment entity from an AssignmentDTO
     * @param assignment The Assignment entity to update
     * @param dto The AssignmentDTO with updated values
     */
    public void updateEntityFromDTO(Assignment assignment, AssignmentDTO dto) {
        if (dto == null) {
            return;
        }
        
        assignment.setTitle(dto.getTitle());
        assignment.setDescription(dto.getDescription());
        assignment.setInstructions(dto.getInstructions());
        assignment.setDueDate(dto.getDueDate());
        if (dto.getMaxPoints() != null) {
            assignment.setMaxPoints(new BigDecimal(dto.getMaxPoints()));
        }
         // Map DTO single attachmentUrl into entity attachments collection
         if (dto.getAttachmentUrl() != null && !dto.getAttachmentUrl().isBlank()) {
             Set<AssignmentAttachment> atts = assignment.getAttachments();
             if (atts == null) {
                 atts = new HashSet<>();
                 assignment.setAttachments(atts);
             }
             // Replace existing with a single attachment representing the DTO field
             atts.clear();
             AssignmentAttachment aa = new AssignmentAttachment();
             aa.setUrl(dto.getAttachmentUrl());
             atts.add(aa);
         }
         assignment.setIsPublished(dto.isPublished());
        
        // Note: Course relationship should be set separately
        // Note: Submissions should be managed separately
    }
    
    /**
     * Convert a list of Assignment entities to a list of AssignmentDTOs
     * @param assignments The list of Assignment entities
     * @return The list of AssignmentDTOs
     */
    public List<AssignmentDTO> toDTOList(List<Assignment> assignments) {
        if (assignments == null) {
            return List.of();
        }
        
        return assignments.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert a set of Assignment entities to a set of AssignmentDTOs
     * @param assignments The set of Assignment entities
     * @return The set of AssignmentDTOs
     */
    public Set<AssignmentDTO> toDTOSet(Set<Assignment> assignments) {
        if (assignments == null) {
            return new HashSet<>();
        }
        
        return assignments.stream()
                .map(this::toDTO)
                .collect(Collectors.toSet());
    }
    
    /**
     * Convert a list of AssignmentDTOs to a list of Assignment entities
     * @param dtos The list of AssignmentDTOs
     * @return The list of Assignment entities
     */
    public List<Assignment> toEntityList(List<AssignmentDTO> dtos) {
        if (dtos == null) {
            return List.of();
        }
        
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert a set of AssignmentDTOs to a set of Assignment entities
     * @param dtos The set of AssignmentDTOs
     * @return The set of Assignment entities
     */
    public Set<Assignment> toEntitySet(Set<AssignmentDTO> dtos) {
        if (dtos == null) {
            return new HashSet<>();
        }
        
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toSet());
    }
}