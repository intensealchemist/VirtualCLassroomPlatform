package com.virtualclassroom.config;

import com.virtualclassroom.model.Role;
import com.virtualclassroom.model.User;
import com.virtualclassroom.model.Course;
import com.virtualclassroom.model.CourseCategory;
import com.virtualclassroom.model.CourseLevel;
import com.virtualclassroom.model.CourseStatus;

import com.virtualclassroom.repository.CourseCategoryRepository;
import com.virtualclassroom.repository.CourseRepository;
import com.virtualclassroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Initializes the database with sample data for development and testing purposes.
 * This configuration is only active in the "dev" profile.
 */
@Configuration
@Profile("dev")
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private CourseCategoryRepository categoryRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            // Only seed if the database is empty
            if (userRepository.count() == 0) {
                seedUsers();
                seedCategories();
                seedCourses();
                
                System.out.println("Database initialized with sample data");
            }
        };
    }
    
    private void seedUsers() {
        // Create admin user
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@virtualclassroom.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setRole(Role.ADMIN);
        admin.setIsEnabled(true);
        admin.setCreatedAt(LocalDateTime.now());
        
        // Create instructor user
        User instructor = new User();
        instructor.setUsername("instructor");
        instructor.setEmail("instructor@virtualclassroom.com");
        instructor.setPassword(passwordEncoder.encode("instructor123"));
        instructor.setFirstName("John");
        instructor.setLastName("Doe");
        instructor.setRole(Role.INSTRUCTOR);
        instructor.setIsEnabled(true);
        instructor.setCreatedAt(LocalDateTime.now());
        
        // Create student user
        User student = new User();
        student.setUsername("student");
        student.setEmail("student@virtualclassroom.com");
        student.setPassword(passwordEncoder.encode("student123"));
        student.setFirstName("Jane");
        student.setLastName("Smith");
        student.setRole(Role.STUDENT);
        student.setIsEnabled(true);
        student.setCreatedAt(LocalDateTime.now());
        
        userRepository.saveAll(Arrays.asList(admin, instructor, student));
    }
    
    private void seedCategories() {
        List<CourseCategory> categories = Arrays.asList(
            new CourseCategory("Programming", "Learn programming languages and software development"),
            new CourseCategory("Mathematics", "Explore mathematical concepts and problem-solving"),
            new CourseCategory("Science", "Discover scientific principles and theories"),
            new CourseCategory("Languages", "Master new languages and communication skills"),
            new CourseCategory("Business", "Develop business acumen and entrepreneurial skills")
        );
        
        categoryRepository.saveAll(categories);
    }
    
    private void seedCourses() {
        User instructor = userRepository.findByUsername("instructor").orElseThrow();
        List<CourseCategory> categories = categoryRepository.findAll();
        
        Course javaCourse = new Course();
        javaCourse.setTitle("Java Programming Fundamentals");
        javaCourse.setDescription("Learn the basics of Java programming language, including syntax, data types, control structures, and object-oriented programming concepts.");
        javaCourse.setInstructor(instructor);
        javaCourse.setCategory(categories.get(0)); // Programming category
        javaCourse.setLevel(CourseLevel.BEGINNER);
        javaCourse.setStatus(CourseStatus.PUBLISHED);
        javaCourse.setCreatedAt(LocalDateTime.now());
        
        Course webDevCourse = new Course();
        webDevCourse.setTitle("Web Development with HTML, CSS, and JavaScript");
        webDevCourse.setDescription("Master the essential technologies for front-end web development and create responsive, interactive websites.");
        webDevCourse.setInstructor(instructor);
        webDevCourse.setCategory(categories.get(0)); // Programming category
        webDevCourse.setLevel(CourseLevel.INTERMEDIATE);
        webDevCourse.setStatus(CourseStatus.PUBLISHED);
        webDevCourse.setCreatedAt(LocalDateTime.now());
        
        courseRepository.saveAll(Arrays.asList(javaCourse, webDevCourse));
    }
}