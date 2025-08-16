package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    
    Optional<LessonProgress> findByStudentIdAndLessonId(Long studentId, Long lessonId);
    
    List<LessonProgress> findByStudentId(Long studentId);
    
    List<LessonProgress> findByLessonId(Long lessonId);
    
    @Query("SELECT lp FROM LessonProgress lp WHERE lp.student.id = :studentId AND lp.lesson.course.id = :courseId")
    List<LessonProgress> findByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(lp) FROM LessonProgress lp WHERE lp.student.id = :studentId AND lp.lesson.course.id = :courseId AND lp.isCompleted = true")
    Long countCompletedLessonsByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
    
    @Query("SELECT AVG(lp.completionPercentage) FROM LessonProgress lp WHERE lp.lesson.course.id = :courseId")
    Double findAverageProgressByCourse(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(lp) FROM LessonProgress lp WHERE lp.student.id = :studentId AND lp.completionPercentage = :percentage")
    Long countByStudentIdAndCompletionPercentage(@Param("studentId") Long studentId, @Param("percentage") Double percentage);
    
    @Query("SELECT lp FROM LessonProgress lp WHERE lp.student.id = :studentId AND lp.isCompleted = false ORDER BY lp.lastAccessed DESC")
    List<LessonProgress> findIncompleteByStudent(@Param("studentId") Long studentId);
    
    @Query("SELECT AVG(lp.completionPercentage) FROM LessonProgress lp WHERE lp.lesson.course.id = :courseId")
    Double getAverageCompletionByCourse(@Param("courseId") Long courseId);
}
