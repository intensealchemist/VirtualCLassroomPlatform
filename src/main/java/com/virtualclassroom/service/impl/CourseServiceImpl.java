package com.virtualclassroom.service.impl;

import com.virtualclassroom.dto.CourseDTO;
import com.virtualclassroom.model.*;
import com.virtualclassroom.repository.CourseRepository;
import com.virtualclassroom.repository.CourseCategoryRepository;
import com.virtualclassroom.repository.CourseEnrollmentRepository;
import com.virtualclassroom.service.ICourseService;
import com.virtualclassroom.service.FileStorageService;
import com.virtualclassroom.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of CourseService interface
 */
@Service
@Transactional
public class CourseServiceImpl implements ICourseService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private CourseEnrollmentRepository enrollmentRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private CourseCategoryRepository courseCategoryRepository;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Override
    public Page<CourseDTO> getAllPublicCourses(Pageable pageable) {
        Page<Course> coursePage = courseRepository.findPublicCourses(pageable);
        List<CourseDTO> courseDTOs = coursePage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(courseDTOs, pageable, coursePage.getTotalElements());
    }
    
    @Override
    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        return convertToDTO(course);
    }
    
    @Override
    public CourseDTO createCourse(CourseDTO courseDTO, User instructor) {
        Course course = new Course();
        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());
        course.setInstructor(instructor);
        course.setStatus(CourseStatus.DRAFT);
        course.setIsPublic(courseDTO.isPublic());
        
        // Generate a unique course code
        String courseCode = generateUniqueCourseCode(courseDTO.getTitle());
        course.setCourseCode(courseCode);
        
        // Set category if provided
        if (courseDTO.getCategoryId() != null) {
            CourseCategory category = courseCategoryRepository.findById(courseDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            course.setCategory(category);
        }
        
        Course savedCourse = courseRepository.save(course);
        return convertToDTO(savedCourse);
    }
    
    @Override
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO, User instructor) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        
        if (!canEditCourse(course, instructor)) {
            throw new RuntimeException("You don't have permission to edit this course");
        }
        
        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());
        course.setIsPublic(courseDTO.isPublic());
        
        // Update category if provided
        if (courseDTO.getCategoryId() != null) {
            CourseCategory category = courseCategoryRepository.findById(courseDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            course.setCategory(category);
        }
        
        Course updatedCourse = courseRepository.save(course);
        return convertToDTO(updatedCourse);
    }
    
    @Override
    public void deleteCourse(Long id, User instructor) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        
        if (!canEditCourse(course, instructor)) {
            throw new RuntimeException("You don't have permission to delete this course");
        }
        
        courseRepository.delete(course);
    }
    
    @Override
    public CourseDTO uploadCoverImage(Long id, MultipartFile file, User instructor) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        
        if (!canEditCourse(course, instructor)) {
            throw new RuntimeException("You don't have permission to update this course");
        }
        
        String coverImageUrl = fileStorageService.storeCourseResource(file);
        course.setCoverImageUrl(coverImageUrl);
        
        Course updatedCourse = courseRepository.save(course);
        return convertToDTO(updatedCourse);
    }
    
    @Override
    public void enrollStudent(Long courseId, User student, String enrollmentKey) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
        
        // Check if enrollment is open
        if (!course.isEnrollmentOpen()) {
            throw new RuntimeException("Enrollment is not open for this course");
        }
        
        // Check if already enrolled
        Optional<CourseEnrollment> existingEnrollment = 
            enrollmentRepository.findByStudentAndCourse(student, course);
        
        if (existingEnrollment.isPresent()) {
            CourseEnrollment enrollment = existingEnrollment.get();
            if (enrollment.getStatus() == EnrollmentStatus.ACTIVE) {
                throw new RuntimeException("Already enrolled in this course");
            } else {
                // Reactivate enrollment
                enrollment.setStatus(EnrollmentStatus.ACTIVE);
                enrollment.setEnrollmentDate(LocalDateTime.now());
                enrollmentRepository.save(enrollment);
                return;
            }
        }
        
        CourseEnrollment enrollment = new CourseEnrollment(student, course);
        enrollmentRepository.save(enrollment);
        
        // Create notification
        notificationService.createNotification(
            "Course Enrollment",
            "You have successfully enrolled in " + course.getTitle(),
            NotificationType.ENROLLMENT,
            student,
            course
        );
    }
    
    @Override
    public void unenrollStudent(Long courseId, User student) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
        
        CourseEnrollment enrollment = enrollmentRepository.findByStudentAndCourse(student, course)
            .orElseThrow(() -> new RuntimeException("Not enrolled in this course"));
        
        enrollment.setStatus(EnrollmentStatus.DROPPED);
        enrollmentRepository.save(enrollment);
    }
    
    @Override
    public List<CourseDTO> getCoursesByInstructor(User instructor) {
        List<Course> courses = courseRepository.findByInstructor(instructor);
        return courses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CourseDTO> getEnrolledCourses(User student) {
        List<Course> courses = courseRepository.findEnrolledCourses(student);
        return courses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private CourseDTO convertToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setInstructorId(course.getInstructor().getId());
        dto.setInstructorName(course.getInstructor().getFullName());
        
        if (course.getCategory() != null) {
            dto.setCategoryId(course.getCategory().getId());
            dto.setCategoryName(course.getCategory().getName());
        }
        
        dto.setCreatedAt(course.getCreatedAt());
        dto.setUpdatedAt(course.getUpdatedAt());
        dto.setActive(course.isActive());
        dto.setCoverImage(course.getCoverImageUrl());
        dto.setPublic(course.getIsPublic());
        
        // Set counts
        dto.setEnrolledStudentsCount(course.getEnrollmentCount());
        dto.setLessonsCount(course.getLessons().size());
        dto.setAssignmentsCount(course.getAssignments().size());
        
        return dto;
    }
    
    private Course convertToEntity(CourseDTO courseDTO) {
        Course course = new Course();
        course.setTitle(courseDTO.getTitle());
        course.setDescription(courseDTO.getDescription());
        course.setIsPublic(courseDTO.isPublic());
        
        if (courseDTO.getCoverImage() != null) {
            course.setCoverImageUrl(courseDTO.getCoverImage());
        }
        
        return course;
    }
    
    private boolean canEditCourse(Course course, User user) {
        return user.isAdmin() || course.getInstructor().equals(user);
    }
    
    private String generateUniqueCourseCode(String title) {
        // Generate a code based on the title
        String baseCode = title.replaceAll("[^a-zA-Z0-9]", "")
                .toUpperCase()
                .substring(0, Math.min(title.length(), 5));
        
        // Add a random number to ensure uniqueness
        String courseCode = baseCode + "-" + (int)(Math.random() * 10000);
        
        // Check if the code already exists
        while (courseRepository.existsByCourseCode(courseCode)) {
            courseCode = baseCode + "-" + (int)(Math.random() * 10000);
        }
        
        return courseCode;
    }
}