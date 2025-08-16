package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussionForumRepository extends JpaRepository<DiscussionForum, Long> {
    
    List<DiscussionForum> findByCourseIdAndIsActiveTrueOrderByIsPinnedDescCreatedAtDesc(Long courseId);
    
    List<DiscussionForum> findByCreatedByIdOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT df FROM DiscussionForum df WHERE df.course.id = :courseId AND df.isActive = true AND (df.title LIKE %:keyword% OR df.description LIKE %:keyword%) ORDER BY df.isPinned DESC, df.createdAt DESC")
    List<DiscussionForum> findByCourseAndKeyword(@Param("courseId") Long courseId, @Param("keyword") String keyword);
    
    @Query("SELECT COUNT(df) FROM DiscussionForum df WHERE df.course.id = :courseId AND df.isActive = true")
    Long countActiveByCourse(@Param("courseId") Long courseId);
    
    List<DiscussionForum> findByIsPinnedTrueAndIsActiveTrueOrderByCreatedAtDesc();
    
    @Query("SELECT COUNT(df) FROM DiscussionForum df WHERE df.course.instructor.id = :instructorId AND df.isActive = true")
    Long countByInstructorId(@Param("instructorId") Long instructorId);
}
