package com.virtualclassroom.repository;

import com.virtualclassroom.model.Course;
import com.virtualclassroom.model.CourseEnrollment;
import com.virtualclassroom.model.EnrollmentStatus;
import com.virtualclassroom.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
    
    Optional<CourseEnrollment> findByStudentAndCourse(User student, Course course);
    
    List<CourseEnrollment> findByStudent(User student);
    
    List<CourseEnrollment> findByCourse(Course course);
    
    List<CourseEnrollment> findByStatus(EnrollmentStatus status);
    
    List<CourseEnrollment> findByCourseAndStatus(Course course, EnrollmentStatus status);
    
    List<CourseEnrollment> findByStudentAndStatus(User student, EnrollmentStatus status);
    
    @Query("SELECT e FROM CourseEnrollment e WHERE e.student = :student AND e.status = 'ACTIVE' ORDER BY e.lastAccessed DESC")
    List<CourseEnrollment> findActiveEnrollmentsByStudent(@Param("student") User student);
    
    @Query("SELECT e FROM CourseEnrollment e WHERE e.course = :course AND e.status = 'ACTIVE' ORDER BY e.enrollmentDate DESC")
    List<CourseEnrollment> findActiveEnrollmentsByCourse(@Param("course") Course course);
    
    @Query("SELECT e FROM CourseEnrollment e WHERE e.status = 'COMPLETED' AND e.completionDate BETWEEN :startDate AND :endDate")
    List<CourseEnrollment> findCompletedEnrollmentsBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                                               @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(e) FROM CourseEnrollment e WHERE e.course = :course AND e.status = :status")
    long countByCourseAndStatus(@Param("course") Course course, @Param("status") EnrollmentStatus status);
    
    @Query("SELECT COUNT(e) FROM CourseEnrollment e WHERE e.student = :student AND e.status = 'COMPLETED'")
    long countCompletedCoursesByStudent(@Param("student") User student);
    
    @Query("SELECT AVG(e.progressPercentage) FROM CourseEnrollment e WHERE e.course = :course AND e.status = 'ACTIVE'")
    Double getAverageProgressByCourse(@Param("course") Course course);
    
    @Query("SELECT e FROM CourseEnrollment e WHERE e.progressPercentage >= 100 AND e.status != 'COMPLETED'")
    List<CourseEnrollment> findEnrollmentsReadyForCompletion();
}
