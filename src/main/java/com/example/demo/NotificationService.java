package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Notification createNotification(User user, String title, String message, NotificationType type) {
        Notification notification = new Notification(user, title, message, type);
        Notification savedNotification = notificationRepository.save(notification);
        
        // Send real-time notification via WebSocket
        messagingTemplate.convertAndSendToUser(
            user.getEmail(), 
            "/queue/notifications", 
            savedNotification
        );
        
        return savedNotification;
    }

    public void notifyAssignmentDue(Assignment assignment) {
        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByCourseIdAndIsActiveTrue(assignment.getCourse().getId());
        
        for (CourseEnrollment enrollment : enrollments) {
            createNotification(
                enrollment.getStudent(),
                "Assignment Due Soon",
                String.format("Assignment '%s' is due on %s", assignment.getTitle(), assignment.getDueDate()),
                NotificationType.ASSIGNMENT_DUE
            );
        }
    }

    public void notifyNewLesson(Lesson lesson) {
        List<CourseEnrollment> enrollments = courseEnrollmentRepository.findByCourseIdAndIsActiveTrue(lesson.getCourse().getId());
        
        for (CourseEnrollment enrollment : enrollments) {
            createNotification(
                enrollment.getStudent(),
                "New Lesson Available",
                String.format("New lesson '%s' has been published in %s", lesson.getTitle(), lesson.getCourse().getTitle()),
                NotificationType.NEW_LESSON
            );
        }
    }

    public void notifyCourseEnrollment(User student, Course course) {
        createNotification(
            student,
            "Course Enrollment Confirmed",
            String.format("You have successfully enrolled in '%s'", course.getTitle()),
            NotificationType.COURSE_ENROLLMENT
        );
    }

    public void markAsRead(Long notificationId, Long userId) {
        notificationRepository.findById(notificationId)
            .filter(n -> n.getUser().getId().equals(userId))
            .ifPresent(notification -> {
                notification.setRead(true);
                notificationRepository.save(notification);
            });
    }

    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    @Autowired
    private CourseEnrollmentRepository courseEnrollmentRepository;
}
