package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    List<CourseEnrollment> findByStudentIdAndIsActiveTrue(Long studentId);

    List<CourseEnrollment> findByCourseIdAndIsActiveTrue(Long courseId);

    Optional<CourseEnrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    @Query("SELECT COUNT(e) FROM CourseEnrollment e WHERE e.course.id = :courseId AND e.isActive = true")
    Long countActiveEnrollmentsByCourse(@Param("courseId") Long courseId);

    @Query("SELECT e FROM CourseEnrollment e WHERE e.student.id = :studentId AND e.isActive = true")
    List<CourseEnrollment> findActiveEnrollmentsByStudent(@Param("studentId") Long studentId);

    @Query("SELECT e FROM CourseEnrollment e WHERE e.course.instructor.id = :instructorId AND e.isActive = true")
    List<CourseEnrollment> findEnrollmentsByInstructor(@Param("instructorId") Long instructorId);

    @Query("SELECT AVG(ce.completionPercentage) FROM CourseEnrollment ce WHERE ce.course.id = :courseId AND ce.isActive = true")
    Double findAverageProgressByCourse(@Param("courseId") Long courseId);

    Long countByIsActiveTrue();

    @Query("SELECT AVG(ce.completionPercentage) FROM CourseEnrollment ce WHERE ce.isActive = true")
    Double findAverageProgress();

    Long countByStudentIdAndIsActiveTrue(Long studentId);

    @Query("SELECT AVG(ce.completionPercentage) FROM CourseEnrollment ce WHERE ce.student.id = :studentId AND ce.isActive = true")
    Double findAverageProgressByStudent(@Param("studentId") Long studentId);

    List<CourseEnrollment> findByStudentIdAndIsActiveTrueOrderByEnrolledAtDesc(Long studentId);

    @Query("SELECT COUNT(DISTINCT ce.student.id) FROM CourseEnrollment ce WHERE ce.course.instructor.id = :instructorId AND ce.isActive = true")
    Long countStudentsByInstructor(@Param("instructorId") Long instructorId);

    @Query("SELECT AVG(ce.completionPercentage) FROM CourseEnrollment ce WHERE ce.course.instructor.id = :instructorId AND ce.isActive = true")
    Double findAverageProgressByInstructor(@Param("instructorId") Long instructorId);

    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.course.instructor.id = :instructorId ORDER BY ce.enrolledAt DESC")
    List<CourseEnrollment> findByInstructorOrderByEnrolledAtDesc(@Param("instructorId") Long instructorId);

    @Query("SELECT AVG(ce.completionPercentage) FROM CourseEnrollment ce WHERE ce.course.id = :courseId AND ce.isActive = true")
    Double getAverageCompletionByCourse(@Param("courseId") Long courseId);
}
