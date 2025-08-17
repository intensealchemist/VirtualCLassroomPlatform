package com.virtualclassroom.service;

import com.virtualclassroom.model.*;
import com.virtualclassroom.repository.CourseRepository;
import com.virtualclassroom.repository.CourseEnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private CourseEnrollmentRepository enrollmentRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private EmailService emailService;
    
    public Course createCourse(String title, String courseCode, String description, 
                              User instructor, CourseCategory category) {
        
        if (courseRepository.existsByCourseCode(courseCode)) {
            throw new RuntimeException("Course code already exists: " + courseCode);
        }
        
        Course course = new Course(title, courseCode, description, instructor);
        course.setCategory(category);
        course.setStatus(CourseStatus.DRAFT);
        
        return courseRepository.save(course);
    }
    
    public Course updateCourse(Long courseId, String title, String description, 
                              CourseCategory category, User currentUser) {
        Course course = getCourseById(courseId);
        
        if (!canEditCourse(course, currentUser)) {
            throw new RuntimeException("You don't have permission to edit this course");
        }
        
        course.setTitle(title);
        course.setDescription(description);
        course.setCategory(category);
        
        return courseRepository.save(course);
    }
    
    public Course publishCourse(Long courseId, User currentUser) {
        Course course = getCourseById(courseId);
        
        if (!canEditCourse(course, currentUser)) {
            throw new RuntimeException("You don't have permission to publish this course");
        }
        
        course.setStatus(CourseStatus.PUBLISHED);
        Course savedCourse = courseRepository.save(course);
        
        // Notify enrolled students
        notifyStudentsOfCourseUpdate(savedCourse, "Course Published", 
            "The course " + course.getTitle() + " has been published and is now available.");
        
        return savedCourse;
    }
    
    public CourseEnrollment enrollStudent(Long courseId, User student) {
        Course course = getCourseById(courseId);
        
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
                return enrollmentRepository.save(enrollment);
            }
        }
        
        CourseEnrollment enrollment = new CourseEnrollment(student, course);
        CourseEnrollment savedEnrollment = enrollmentRepository.save(enrollment);
        
        // Send confirmation email
        emailService.sendCourseEnrollmentEmail(student, course.getTitle());
        
        // Create notification
        notificationService.createNotification(
            "Course Enrollment",
            "You have successfully enrolled in " + course.getTitle(),
            NotificationType.ENROLLMENT,
            student,
            course
        );
        
        return savedEnrollment;
    }
    
    public void unenrollStudent(Long courseId, User student) {
        Course course = getCourseById(courseId);
        CourseEnrollment enrollment = enrollmentRepository.findByStudentAndCourse(student, course)
            .orElseThrow(() -> new RuntimeException("Not enrolled in this course"));
        
        enrollment.setStatus(EnrollmentStatus.DROPPED);
        enrollmentRepository.save(enrollment);
    }
    
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
    }
    
    public Course getCourseByCourseCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode)
            .orElseThrow(() -> new RuntimeException("Course not found with code: " + courseCode));
    }
    
    public Page<Course> getPublicCourses(Pageable pageable) {
        return courseRepository.findPublicCourses(pageable);
    }
    
    public List<Course> getFeaturedCourses() {
        return courseRepository.findFeaturedCourses();
    }
    
    public Page<Course> searchCourses(String query, Pageable pageable) {
        return courseRepository.searchCourses(query, pageable);
    }
    
    public Page<Course> getCoursesByCategory(Long categoryId, Pageable pageable) {
        return courseRepository.findByCategoryId(categoryId, pageable);
    }
    
    public Page<Course> getCoursesByInstructor(User instructor, Pageable pageable) {
        return courseRepository.findByInstructor(instructor, pageable);
    }
    
    public List<Course> getEnrolledCourses(User student) {
        return courseRepository.findEnrolledCourses(student);
    }
    
    public List<Course> getRecentCourses(int limit) {
        return courseRepository.findRecentCourses(Pageable.ofSize(limit));
    }
    
    public List<Course> getPopularCourses(int limit) {
        return courseRepository.findPopularCourses(Pageable.ofSize(limit));
    }
    
    public CourseEnrollment getEnrollment(User student, Course course) {
        return enrollmentRepository.findByStudentAndCourse(student, course)
            .orElse(null);
    }
    
    public boolean isStudentEnrolled(User student, Course course) {
        return enrollmentRepository.findByStudentAndCourse(student, course)
            .map(enrollment -> enrollment.getStatus() == EnrollmentStatus.ACTIVE)
            .orElse(false);
    }
    
    public void updateCourseProgress(User student, Course course, double progressPercentage) {
        CourseEnrollment enrollment = enrollmentRepository.findByStudentAndCourse(student, course)
            .orElseThrow(() -> new RuntimeException("Student not enrolled in course"));
        
        enrollment.updateProgress(progressPercentage);
        enrollmentRepository.save(enrollment);
        
        // Check if course is completed
        if (progressPercentage >= 100.0) {
            notificationService.createNotification(
                "Course Completed",
                "Congratulations! You have completed " + course.getTitle(),
                NotificationType.SUCCESS,
                student,
                course
            );
        }
    }
    
    public long getCourseCount() {
        return courseRepository.count();
    }
    
    public long getPublishedCourseCount() {
        return courseRepository.countByStatus(CourseStatus.PUBLISHED);
    }
    
    public long getInstructorCourseCount(User instructor) {
        return courseRepository.countByInstructor(instructor);
    }
    
    public long getCourseEnrollmentCount(Course course) {
        return courseRepository.countActiveEnrollments(course);
    }
    
    private boolean canEditCourse(Course course, User user) {
        return user.isAdmin() || course.getInstructor().equals(user);
    }
    
    private void notifyStudentsOfCourseUpdate(Course course, String title, String message) {
        List<CourseEnrollment> enrollments = enrollmentRepository.findByCourseAndStatus(course, EnrollmentStatus.ACTIVE);
        
        for (CourseEnrollment enrollment : enrollments) {
            notificationService.createNotification(
                title,
                message,
                NotificationType.COURSE,
                enrollment.getStudent(),
                course
            );
        }
    }
}
