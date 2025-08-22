package com.virtualclassroom.repository;

import com.virtualclassroom.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Lesson entity
 */
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    
    /**
     * Find all lessons for a course ordered by lesson order
     * @param courseId The course ID
     * @return List of lessons
     */
    List<Lesson> findByCourseIdOrderByLessonOrderAsc(Long courseId);
    
    /**
     * Find a lesson by ID and course ID
     * @param id The lesson ID
     * @param courseId The course ID
     * @return Optional containing the lesson if found
     */
    Optional<Lesson> findByIdAndCourseId(Long id, Long courseId);
    
    /**
     * Find published lessons for a course ordered by lesson order
     * @param courseId The course ID
     * @return List of published lessons
     */
    List<Lesson> findByCourseIdAndIsPublishedTrueOrderByLessonOrderAsc(Long courseId);
    
    /**
     * Count lessons for a course
     * @param courseId The course ID
     * @return The number of lessons
     */
    long countByCourseId(Long courseId);
    
    /**
     * Count published lessons for a course
     * @param courseId The course ID
     * @return The number of published lessons
     */
    long countByCourseIdAndIsPublishedTrue(Long courseId);
    
    /**
     * Find the maximum lesson order for a course
     * @param courseId The course ID
     * @return The maximum lesson order or 0 if no lessons exist
     */
    @Query("SELECT COALESCE(MAX(l.lessonOrder), 0) FROM Lesson l WHERE l.course.id = :courseId")
    int findMaxLessonOrderByCourseId(@Param("courseId") Long courseId);
    
    /**
     * Update lesson orders for reordering
     * @param lessonId The lesson ID
     * @param newOrder The new order
     * @return The number of affected rows
     */
    @Modifying
    @Query("UPDATE Lesson l SET l.lessonOrder = :newOrder WHERE l.id = :lessonId")
    int updateLessonOrder(@Param("lessonId") Long lessonId, @Param("newOrder") int newOrder);
    
    /**
     * Shift lesson orders for a course when inserting or removing a lesson
     * @param courseId The course ID
     * @param startOrder The starting order to shift from
     * @param shiftAmount The amount to shift (positive or negative)
     * @return The number of affected rows
     */
    @Modifying
    @Query("UPDATE Lesson l SET l.lessonOrder = l.lessonOrder + :shiftAmount " +
           "WHERE l.course.id = :courseId AND l.lessonOrder >= :startOrder")
    int shiftLessonOrders(
            @Param("courseId") Long courseId,
            @Param("startOrder") int startOrder,
            @Param("shiftAmount") int shiftAmount);
}