package com.virtualclassroom.service;

import com.virtualclassroom.model.*;
import com.virtualclassroom.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    public Notification createNotification(String title, String message, NotificationType type, User user) {
        Notification notification = new Notification(title, message, type, user);
        return notificationRepository.save(notification);
    }
    
    public Notification createNotification(String title, String message, NotificationType type, 
                                         User user, Course course) {
        Notification notification = new Notification(title, message, type, user);
        notification.setCourse(course);
        return notificationRepository.save(notification);
    }
    
    public Notification createNotification(String title, String message, NotificationType type, 
                                         User user, Assignment assignment) {
        Notification notification = new Notification(title, message, type, user);
        notification.setAssignment(assignment);
        notification.setCourse(assignment.getCourse());
        return notificationRepository.save(notification);
    }
    
    public Notification createNotificationWithAction(String title, String message, NotificationType type, 
                                                   User user, String actionUrl, String actionText) {
        Notification notification = new Notification(title, message, type, user);
        notification.setActionUrl(actionUrl);
        notification.setActionText(actionText);
        return notificationRepository.save(notification);
    }
    
    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    public Page<Notification> getUserNotifications(User user, Pageable pageable) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }
    
    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
    }
    
    public long getUnreadNotificationCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }
    
    public void markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        if (!notification.getUser().equals(user)) {
            throw new RuntimeException("You don't have permission to access this notification");
        }
        
        notification.markAsRead();
        notificationRepository.save(notification);
    }
    
    public void markAllAsRead(User user) {
        List<Notification> unreadNotifications = getUnreadNotifications(user);
        for (Notification notification : unreadNotifications) {
            notification.markAsRead();
        }
        notificationRepository.saveAll(unreadNotifications);
    }
    
    public void deleteNotification(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        if (!notification.getUser().equals(user)) {
            throw new RuntimeException("You don't have permission to delete this notification");
        }
        
        notificationRepository.delete(notification);
    }
    
    public void deleteExpiredNotifications() {
        List<Notification> expiredNotifications = notificationRepository.findExpiredNotifications(LocalDateTime.now());
        notificationRepository.deleteAll(expiredNotifications);
    }
    
    // Bulk notification methods for system events
    public void notifyAllUsers(String title, String message, NotificationType type) {
        // This would typically be done in batches for performance
        // Implementation depends on user base size
    }
    
    public void notifyInstructors(String title, String message, NotificationType type) {
        // Notify all instructors
    }
    
    public void notifyStudents(String title, String message, NotificationType type) {
        // Notify all students
    }
}
