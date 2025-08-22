package com.virtualclassroom.mapper;

import com.virtualclassroom.dto.SubmissionDTO;
import com.virtualclassroom.model.Submission;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Submission entities and DTOs
 */
@Component
public class SubmissionMapper {

    /**
     * Convert a Submission entity to a SubmissionDTO
     * @param submission The Submission entity
     * @return The SubmissionDTO
     */
    public SubmissionDTO toDTO(Submission submission) {
        if (submission == null) {
            return null;
        }
        
        SubmissionDTO dto = new SubmissionDTO();
        dto.setId(submission.getId());
        
        if (submission.getAssignment() != null) {
            dto.setAssignmentId(submission.getAssignment().getId());
            dto.setAssignmentTitle(submission.getAssignment().getTitle());
            
            if (submission.getAssignment().getCourse() != null) {
                dto.setCourseId(submission.getAssignment().getCourse().getId());
                dto.setCourseName(submission.getAssignment().getCourse().getTitle());
            }
        }
        
        if (submission.getStudent() != null) {
            dto.setStudentId(submission.getStudent().getId());
            dto.setStudentName(submission.getStudent().getFullName());
        }
        
        dto.setContent(submission.getContent());
        dto.setAttachmentUrl(submission.getAttachmentUrl());
        dto.setGrade(submission.getGrade());
        dto.setFeedback(submission.getFeedback());
        dto.setSubmittedAt(submission.getSubmittedAt());
        dto.setGradedAt(submission.getGradedAt());
        dto.setLate(submission.isLate());
        
        return dto;
    }
    
    /**
     * Convert a SubmissionDTO to a Submission entity
     * @param dto The SubmissionDTO
     * @return The Submission entity
     */
    public Submission toEntity(SubmissionDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Submission submission = new Submission();
        updateEntityFromDTO(submission, dto);
        return submission;
    }
    
    /**
     * Update a Submission entity from a SubmissionDTO
     * @param submission The Submission entity to update
     * @param dto The SubmissionDTO with updated values
     */
    public void updateEntityFromDTO(Submission submission, SubmissionDTO dto) {
        if (dto == null) {
            return;
        }
        
        submission.setContent(dto.getContent());
        submission.setAttachmentUrl(dto.getAttachmentUrl());
        submission.setGrade(dto.getGrade());
        submission.setFeedback(dto.getFeedback());
        
        // Note: Assignment and Student relationships should be set separately
        // Note: Timestamps and late status are managed by the entity itself
    }
    
    /**
     * Convert a list of Submission entities to a list of SubmissionDTOs
     * @param submissions The list of Submission entities
     * @return The list of SubmissionDTOs
     */
    public List<SubmissionDTO> toDTOList(List<Submission> submissions) {
        if (submissions == null) {
            return List.of();
        }
        
        return submissions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert a list of SubmissionDTOs to a list of Submission entities
     * @param dtos The list of SubmissionDTOs
     * @return The list of Submission entities
     */
    public List<Submission> toEntityList(List<SubmissionDTO> dtos) {
        if (dtos == null) {
            return List.of();
        }
        
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}