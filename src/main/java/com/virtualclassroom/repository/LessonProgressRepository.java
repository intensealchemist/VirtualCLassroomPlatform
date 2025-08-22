package com.virtualclassroom.repository;

import com.virtualclassroom.model.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for LessonProgress entity
 */
@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    /**
     * Find a progress record by lesson ID and student ID
     * @param lessonId The lesson ID
     * @param studentId The student ID
     * @return Optional containing the progress record if found
     */
    Optional<LessonProgress> findByLessonIdAndStudentId(Long lessonId, Long studentId);

    /**
     * Find all progress records for a student in a course
     * @param courseId The course ID
     * @param studentId The student ID
     * @return List of progress records
     */
    @Query("SELECT lp FROM LessonProgress lp JOIN lp.lesson l WHERE l.course.id = :courseId AND lp.student.id = :studentId")
    List<LessonProgress> findByCourseIdAndStudentId(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

    /**
     * Count completed lessons in a course for a student
     * @param courseId The course ID
     * @param studentId The student ID
     * @return Number of completed lessons
     */
    @Query("SELECT COUNT(lp) FROM LessonProgress lp JOIN lp.lesson l WHERE l.course.id = :courseId AND lp.student.id = :studentId AND lp.isCompleted = true")
    Integer countCompletedLessonsByCourseAndStudent(@Param("courseId") Long courseId, @Param("studentId") Long studentId);

    /**
     * Calculate average progress percentage for a course by a student
     * @param courseId The course ID
     * @param studentId The student ID
     * @return Average progress percentage
     */
    @Query("SELECT AVG(lp.progressPercentage) FROM LessonProgress lp JOIN lp.lesson l WHERE l.course.id = :courseId AND lp.student.id = :studentId")
    Double getAverageProgressForCourseAndStudent(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
}