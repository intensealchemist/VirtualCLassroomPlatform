package com.virtualclassroom.repository;

import com.virtualclassroom.model.LessonResource;
import com.virtualclassroom.model.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for LessonResource entity
 */
@Repository
public interface LessonResourceRepository extends JpaRepository<LessonResource, Long> {

    /**
     * Find all resources for a lesson
     * @param lessonId The lesson ID
     * @return List of lesson resources
     */
    List<LessonResource> findByLessonId(Long lessonId);

    /**
     * Find resources by lesson ID and resource type
     * @param lessonId The lesson ID
     * @param resourceType The resource type
     * @return List of lesson resources of the specified type
     */
    List<LessonResource> findByLessonIdAndResourceType(Long lessonId, ResourceType resourceType);

    /**
     * Delete all resources for a lesson
     * @param lessonId The lesson ID
     */
    void deleteByLessonId(Long lessonId);
}