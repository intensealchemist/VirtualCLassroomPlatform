package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    
    List<Lesson> findByCourseIdOrderByLessonOrder(Long courseId);
    
    List<Lesson> findByCourseIdAndIsPublishedTrue(Long courseId);
    
    @Query("SELECT l FROM Lesson l WHERE l.course.id = :courseId AND l.isPublished = true ORDER BY l.lessonOrder")
    List<Lesson> findPublishedLessonsByCourse(@Param("courseId") Long courseId);
    
    // There is no instructor field on Lesson; traverse via course.instructor.id
    @Query("SELECT l FROM Lesson l WHERE l.course.instructor.id = :instructorId")
    List<Lesson> findByInstructorId(@Param("instructorId") Long instructorId);
    
    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.course.id = :courseId")
    Long countLessonsByCourse(@Param("courseId") Long courseId);
    
    @Query("SELECT l FROM Lesson l WHERE l.title LIKE %:keyword% OR l.content LIKE %:keyword% ORDER BY l.createdAt DESC")
    List<Lesson> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.course.instructor.id = :instructorId")
    Long countByInstructorId(@Param("instructorId") Long instructorId);
    
    @Query("SELECT l FROM Lesson l WHERE l.title LIKE %:keyword% OR l.content LIKE %:keyword% ORDER BY l.createdAt DESC")
    List<Lesson> findByKeyword(@Param("keyword") String keyword);
}
