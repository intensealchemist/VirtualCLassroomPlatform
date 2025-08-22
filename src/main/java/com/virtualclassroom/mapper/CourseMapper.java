package com.virtualclassroom.mapper;

import com.virtualclassroom.dto.CourseDTO;
import com.virtualclassroom.model.Course;
import com.virtualclassroom.model.CourseCategory;
import com.virtualclassroom.model.User;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Course entities and DTOs
 */
@Component
public class CourseMapper {

    /**
     * Convert a Course entity to a CourseDTO
     * @param course The course entity
     * @param currentUser The current user (optional, for checking enrollment status)
     * @return The course DTO
     */
    public CourseDTO toDTO(Course course, User currentUser) {
        if (course == null) {
            return null;
        }
        
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        
        // Set instructor information
        if (course.getInstructor() != null) {
            dto.setInstructorId(course.getInstructor().getId());
            dto.setInstructorName(course.getInstructor().getFullName());
        }
        
        // Set category information
        if (course.getCategory() != null) {
            dto.setCategoryId(course.getCategory().getId());
            dto.setCategoryName(course.getCategory().getName());
        }
        
        // Set dates and status
        dto.setCreatedAt(course.getCreatedAt());
        dto.setUpdatedAt(course.getUpdatedAt());
        dto.setActive(course.isActive());
        
        // Set media and enrollment info
        dto.setCoverImage(course.getCoverImageUrl());
        // Enrollment key not implemented on Course entity; leaving null in DTO
        // dto.setEnrollmentKey(course.getEnrollmentKey());
        dto.setPublic(course.getIsPublic());
        
        // Set counts
        dto.setEnrolledStudentsCount(course.getEnrollmentCount());
        dto.setLessonsCount(course.getLessons().size());
        dto.setAssignmentsCount(course.getAssignments().size());
        
        // Check if current user is enrolled
        if (currentUser != null) {
            boolean isEnrolled = course.getEnrollments().stream()
                    .anyMatch(enrollment -> 
                        enrollment.getStudent().getId().equals(currentUser.getId()) && 
                        enrollment.getStatus() == com.virtualclassroom.model.EnrollmentStatus.ACTIVE);
            dto.setEnrolled(isEnrolled);
        }
        
        return dto;
    }
    
    /**
     * Convert a Course entity to a CourseDTO without user-specific information
     * @param course The course entity
     * @return The course DTO
     */
    public CourseDTO toDTO(Course course) {
        return toDTO(course, null);
    }

    /**
     * Convert a CourseDTO to a Course entity
     * @param dto The course DTO
     * @param existingCourse An existing course to update (optional)
     * @param instructor The instructor (required for new courses)
     * @param category The course category (optional)
     * @return The course entity
     */
    public Course toEntity(CourseDTO dto, Course existingCourse, User instructor, CourseCategory category) {
        Course course = existingCourse != null ? existingCourse : new Course();
        
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        
        // Only set instructor for new courses
        if (existingCourse == null && instructor != null) {
            course.setInstructor(instructor);
        }
        
        // Set category if provided
        if (category != null) {
            course.setCategory(category);
        }
        
        // Set visibility
        course.setIsPublic(dto.isPublic());
        
        return course;
    }
    
    /**
     * Convert a CourseDTO to a new Course entity
     * @param dto The course DTO
     * @param instructor The instructor
     * @param category The course category (optional)
     * @return The course entity
     */
    public Course toEntity(CourseDTO dto, User instructor, CourseCategory category) {
        return toEntity(dto, null, instructor, category);
    }
}