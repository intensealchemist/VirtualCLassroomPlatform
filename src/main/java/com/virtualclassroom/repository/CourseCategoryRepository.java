package com.virtualclassroom.repository;

import com.virtualclassroom.model.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Long> {
    Optional<CourseCategory> findByName(String name);

    @Query("SELECT c FROM CourseCategory c WHERE c.isActive = true ORDER BY c.displayOrder ASC, c.name ASC")
    List<CourseCategory> findActiveOrdered();

    @Query("SELECT c FROM CourseCategory c WHERE c.isActive = true AND LOWER(c.name) = LOWER(:name)")
    Optional<CourseCategory> findActiveByNameIgnoreCase(@Param("name") String name);
}
