package com.virtualclassroom.service;

import com.virtualclassroom.dto.CourseDTO;
import com.virtualclassroom.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interface for Course service operations
 */
public interface ICourseService {
    
    /**
     * Get all public courses with pagination
     * @param pageable Pagination information
     * @return Page of CourseDTO objects
     */
    Page<CourseDTO> getAllPublicCourses(Pageable pageable);
    
    /**
     * Get course by ID
     * @param id Course ID
     * @return CourseDTO
     */
    CourseDTO getCourseById(Long id);
    
    /**
     * Create a new course
     * @param courseDTO Course data
     * @param instructor Course instructor
     * @return Created CourseDTO
     */
    CourseDTO createCourse(CourseDTO courseDTO, User instructor);
    
    /**
     * Update an existing course
     * @param id Course ID
     * @param courseDTO Updated course data
     * @param instructor Course instructor
     * @return Updated CourseDTO
     */
    CourseDTO updateCourse(Long id, CourseDTO courseDTO, User instructor);
    
    /**
     * Delete a course
     * @param id Course ID
     * @param instructor Course instructor
     */
    void deleteCourse(Long id, User instructor);
    
    /**
     * Upload a cover image for a course
     * @param id Course ID
     * @param file Cover image file
     * @param instructor Course instructor
     * @return Updated CourseDTO
     */
    CourseDTO uploadCoverImage(Long id, MultipartFile file, User instructor);
    
    /**
     * Enroll a student in a course
     * @param courseId Course ID
     * @param student Student user
     * @param enrollmentKey Enrollment key (optional)
     */
    void enrollStudent(Long courseId, User student, String enrollmentKey);
    
    /**
     * Unenroll a student from a course
     * @param courseId Course ID
     * @param student Student user
     */
    void unenrollStudent(Long courseId, User student);
    
    /**
     * Get courses taught by an instructor
     * @param instructor Instructor user
     * @return List of CourseDTO objects
     */
    List<CourseDTO> getCoursesByInstructor(User instructor);
    
    /**
     * Get courses in which a student is enrolled
     * @param student Student user
     * @return List of CourseDTO objects
     */
    List<CourseDTO> getEnrolledCourses(User student);
}