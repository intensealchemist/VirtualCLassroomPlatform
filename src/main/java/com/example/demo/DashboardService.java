package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private CourseEnrollmentRepository enrollmentRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private DiscussionForumRepository forumRepository;

    @Autowired
    private ForumPostRepository postRepository;

    @Autowired
    private LessonProgressRepository lessonProgressRepository;

    public DashboardStats getAdminDashboardStats() {
        DashboardStats stats = new DashboardStats();
        
        stats.setTotalCourses(courseRepository.count());
        stats.setTotalStudents(userRepository.countByRole(Role.STUDENT));
        stats.setTotalInstructors(userRepository.countByRole(Role.INSTRUCTOR));
        stats.setTotalAssignments(assignmentRepository.count());
        stats.setTotalLessons(lessonRepository.count());
        stats.setActiveEnrollments(enrollmentRepository.countByIsActiveTrue());
        stats.setAverageProgress(enrollmentRepository.findAverageProgress());
        stats.setTotalForums(forumRepository.count());
        stats.setTotalPosts(postRepository.count());
        
        return stats;
    }

    public StudentDashboard getStudentDashboard(Long studentId) {
        StudentDashboard dashboard = new StudentDashboard();
        
        // Basic stats
        dashboard.setEnrolledCourses(enrollmentRepository.countByStudentIdAndIsActiveTrue(studentId));
        dashboard.setCompletedLessons(lessonProgressRepository.countByStudentIdAndCompletionPercentage(studentId, 100.0));
        dashboard.setPendingAssignments(assignmentRepository.countPendingByStudent(studentId, LocalDateTime.now()));
        dashboard.setAverageProgress(enrollmentRepository.findAverageProgressByStudent(studentId));
        dashboard.setUnreadNotifications(notificationRepository.countUnreadByUser(studentId));
        
        // Recent data
        List<CourseEnrollment> enrollments = enrollmentRepository.findByStudentIdAndIsActiveTrueOrderByEnrolledAtDesc(studentId);
        dashboard.setRecentCourses(enrollments.stream().map(CourseEnrollment::getCourse).limit(5).toList());
        
        dashboard.setUpcomingAssignments(assignmentRepository.findUpcomingByStudent(studentId, LocalDateTime.now()));
        dashboard.setRecentNotifications(notificationRepository.findByUserIdOrderByCreatedAtDesc(studentId).stream().limit(5).toList());
        
        return dashboard;
    }

    public InstructorDashboard getInstructorDashboard(Long instructorId) {
        InstructorDashboard dashboard = new InstructorDashboard();
        
        // Basic stats
        dashboard.setTotalCourses(courseRepository.countByInstructorId(instructorId));
        dashboard.setTotalStudents(enrollmentRepository.countStudentsByInstructor(instructorId));
        dashboard.setTotalAssignments(assignmentRepository.countByInstructorId(instructorId));
        dashboard.setTotalLessons(lessonRepository.countByInstructorId(instructorId));
        dashboard.setAverageStudentProgress(enrollmentRepository.findAverageProgressByInstructor(instructorId));
        dashboard.setActiveForums(forumRepository.countByInstructorId(instructorId));
        dashboard.setUnreadNotifications(notificationRepository.countUnreadByUser(instructorId));
        
        // Recent data
        dashboard.setRecentCourses(courseRepository.findByInstructorIdOrderByCreatedAtDesc(instructorId).stream().limit(5).toList());
        dashboard.setRecentAssignments(assignmentRepository.findByInstructorIdOrderByCreatedAtDesc(instructorId).stream().limit(5).toList());
        dashboard.setRecentEnrollments(enrollmentRepository.findByInstructorOrderByEnrolledAtDesc(instructorId).stream().limit(10).toList());
        dashboard.setRecentNotifications(notificationRepository.findByUserIdOrderByCreatedAtDesc(instructorId).stream().limit(5).toList());
        
        return dashboard;
    }
}
