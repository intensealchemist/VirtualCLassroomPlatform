package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {
    
    List<ForumPost> findByForumIdAndParentPostIsNullAndIsDeletedFalseOrderByCreatedAtAsc(Long forumId);
    
    List<ForumPost> findByParentPostIdAndIsDeletedFalseOrderByCreatedAtAsc(Long parentPostId);
    
    List<ForumPost> findByAuthorIdOrderByCreatedAtDesc(Long authorId);
    
    @Query("SELECT fp FROM ForumPost fp WHERE fp.forum.id = :forumId AND fp.isDeleted = false AND fp.content LIKE %:keyword% ORDER BY fp.createdAt DESC")
    List<ForumPost> findByForumAndKeyword(@Param("forumId") Long forumId, @Param("keyword") String keyword);
    
    @Query("SELECT COUNT(fp) FROM ForumPost fp WHERE fp.forum.id = :forumId AND fp.isDeleted = false")
    Long countByForum(@Param("forumId") Long forumId);
    
    @Query("SELECT COUNT(fp) FROM ForumPost fp WHERE fp.parentPost.id = :parentPostId AND fp.isDeleted = false")
    Long countRepliesByParentPost(@Param("parentPostId") Long parentPostId);
    
    @Query("SELECT fp FROM ForumPost fp WHERE fp.forum.course.id = :courseId AND fp.isDeleted = false ORDER BY fp.createdAt DESC")
    List<ForumPost> findByCourseId(@Param("courseId") Long courseId);
}
