package com.virtualclassroom.repository;

import com.virtualclassroom.model.Course;
import com.virtualclassroom.model.CourseStatus;
import com.virtualclassroom.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    Optional<Course> findByCourseCode(String courseCode);
    
    List<Course> findByInstructor(User instructor);
    
    List<Course> findByStatus(CourseStatus status);
    
    Page<Course> findByStatus(CourseStatus status, Pageable pageable);
    
    @Query("SELECT c FROM Course c WHERE c.status = 'PUBLISHED' AND c.isPublic = true")
    Page<Course> findPublicCourses(Pageable pageable);
    
    @Query("SELECT c FROM Course c WHERE c.status = 'PUBLISHED' AND c.isFeatured = true")
    List<Course> findFeaturedCourses();
    
    @Query("SELECT c FROM Course c WHERE " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Course> searchCourses(@Param("query") String query, Pageable pageable);
    
    @Query("SELECT c FROM Course c WHERE c.category.id = :categoryId AND c.status = 'PUBLISHED'")
    Page<Course> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT c FROM Course c WHERE c.instructor = :instructor")
    Page<Course> findByInstructor(@Param("instructor") User instructor, Pageable pageable);
    
    @Query("SELECT COUNT(e) FROM CourseEnrollment e WHERE e.course = :course AND e.status = 'ACTIVE'")
    long countActiveEnrollments(@Param("course") Course course);
    
    @Query("SELECT c FROM Course c JOIN c.enrollments e WHERE e.student = :student AND e.status = 'ACTIVE'")
    List<Course> findEnrolledCourses(@Param("student") User student);
    
    @Query("SELECT c FROM Course c ORDER BY c.createdAt DESC")
    List<Course> findRecentCourses(Pageable pageable);
    
    @Query("SELECT c FROM Course c WHERE SIZE(c.enrollments) > 0 ORDER BY SIZE(c.enrollments) DESC")
    List<Course> findPopularCourses(Pageable pageable);
    
    boolean existsByCourseCode(String courseCode);
    
    long countByInstructor(User instructor);
    
    long countByStatus(CourseStatus status);
}
