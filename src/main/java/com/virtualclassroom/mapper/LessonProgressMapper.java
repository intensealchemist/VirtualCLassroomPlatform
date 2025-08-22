package com.virtualclassroom.mapper;

import com.virtualclassroom.dto.LessonProgressDTO;
import com.virtualclassroom.model.LessonProgress;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between LessonProgress entities and DTOs
 */
@Component
public class LessonProgressMapper {

    /**
     * Convert a LessonProgress entity to a LessonProgressDTO
     * @param lessonProgress The LessonProgress entity
     * @return The LessonProgressDTO
     */
    public LessonProgressDTO toDTO(LessonProgress lessonProgress) {
        if (lessonProgress == null) {
            return null;
        }

        LessonProgressDTO dto = new LessonProgressDTO();
        dto.setId(lessonProgress.getId());
        
        // Lesson information
        if (lessonProgress.getLesson() != null) {
            dto.setLessonId(lessonProgress.getLesson().getId());
            dto.setLessonTitle(lessonProgress.getLesson().getTitle());
            
            // Course information
            if (lessonProgress.getLesson().getCourse() != null) {
                dto.setCourseId(lessonProgress.getLesson().getCourse().getId());
                dto.setCourseName(lessonProgress.getLesson().getCourse().getTitle());
            }
        }
        
        // Student information
        if (lessonProgress.getStudent() != null) {
            dto.setStudentId(lessonProgress.getStudent().getId());
            dto.setStudentName(lessonProgress.getStudent().getFullName());
        }
        
        // Progress details
        dto.setProgressPercentage(lessonProgress.getProgressPercentage());
        dto.setCompleted(lessonProgress.isCompleted());
        dto.setTimeSpentSeconds(lessonProgress.getTimeSpentSeconds());
        dto.setLastPosition(lessonProgress.getLastPosition());
        
        // Timestamps
        dto.setStartedAt(lessonProgress.getStartedAt());
        dto.setCompletedAt(lessonProgress.getCompletedAt());
        dto.setLastAccessedAt(lessonProgress.getLastAccessedAt());
        dto.setCreatedAt(lessonProgress.getCreatedAt());
        dto.setUpdatedAt(lessonProgress.getUpdatedAt());
        
        return dto;
    }

    /**
     * Convert a list of LessonProgress entities to a list of LessonProgressDTOs
     * @param lessonProgresses The list of LessonProgress entities
     * @return The list of LessonProgressDTOs
     */
    public List<LessonProgressDTO> toDTOList(List<LessonProgress> lessonProgresses) {
        if (lessonProgresses == null) {
            return null;
        }
        
        return lessonProgresses.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}