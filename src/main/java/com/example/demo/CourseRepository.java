package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    @Query("SELECT c FROM Course c WHERE c.instructor.id = :instructorId")
    List<Course> findByInstructorId(@Param("instructorId") Long instructorId);
    
    List<Course> findByIsActiveTrue();
    
    @Query("SELECT c FROM Course c WHERE c.title LIKE %:keyword% OR c.description LIKE %:keyword% ORDER BY c.createdAt DESC")
    List<Course> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT COUNT(c) FROM Course c WHERE c.instructor.id = :instructorId")
    Long countByInstructorId(@Param("instructorId") Long instructorId);
    
    @Query("SELECT c FROM Course c WHERE c.instructor.id = :instructorId ORDER BY c.createdAt DESC")
    List<Course> findByInstructorIdOrderByCreatedAtDesc(@Param("instructorId") Long instructorId);
    
    @Query("SELECT c FROM Course c WHERE c.title LIKE %:keyword% OR c.description LIKE %:keyword% ORDER BY c.createdAt DESC")
    List<Course> findByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT c FROM Course c WHERE c.instructor.id = :instructorId AND c.isActive = true")
    List<Course> findActiveByInstructor(@Param("instructorId") Long instructorId);
}
