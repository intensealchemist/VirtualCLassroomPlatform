package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    
    List<Assignment> findByCourseId(Long courseId);
    
    List<Assignment> findByCourseIdAndIsPublishedTrue(Long courseId);
    
    @Query("SELECT a FROM Assignment a WHERE a.course.instructor.id = :instructorId AND a.dueDate BETWEEN :startDate AND :endDate ORDER BY a.dueDate ASC")
    List<Assignment> findByInstructorAndDateRange(@Param("instructorId") Long instructorId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.course.id IN (SELECT ce.course.id FROM CourseEnrollment ce WHERE ce.student.id = :studentId AND ce.isActive = true) AND a.dueDate > :now AND a.isPublished = true")
    Long countPendingByStudent(@Param("studentId") Long studentId, @Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM Assignment a WHERE a.course.id IN (SELECT ce.course.id FROM CourseEnrollment ce WHERE ce.student.id = :studentId AND ce.isActive = true) AND a.dueDate > :now AND a.isPublished = true ORDER BY a.dueDate ASC")
    List<Assignment> findUpcomingByStudent(@Param("studentId") Long studentId, @Param("now") LocalDateTime now);
    
    // Explicitly traverse via course.instructor.id to avoid derived query parsing errors
    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.course.instructor.id = :instructorId")
    Long countByInstructorId(@Param("instructorId") Long instructorId);
    
    @Query("SELECT a FROM Assignment a WHERE a.course.instructor.id = :instructorId ORDER BY a.createdAt DESC")
    List<Assignment> findByInstructorIdOrderByCreatedAtDesc(@Param("instructorId") Long instructorId);
    
    @Query("SELECT a FROM Assignment a WHERE a.course.instructor.id = :instructorId")
    List<Assignment> findByInstructorId(@Param("instructorId") Long instructorId);
    
    @Query("SELECT a FROM Assignment a WHERE a.dueDate BETWEEN :startDate AND :endDate")
    List<Assignment> findByDueDateBetween(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM Assignment a WHERE a.dueDate < :currentDate AND a.isPublished = true")
    List<Assignment> findOverdueAssignments(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT a FROM Assignment a WHERE a.assignmentType = :type")
    List<Assignment> findByAssignmentType(@Param("type") AssignmentType type);
    
    @Query("SELECT COUNT(a) FROM Assignment a WHERE a.course.id = :courseId")
    Long countAssignmentsByCourse(@Param("courseId") Long courseId);
}
