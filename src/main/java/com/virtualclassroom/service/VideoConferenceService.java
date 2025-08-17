package com.virtualclassroom.service;

import com.virtualclassroom.dto.VideoSessionRequest;
import com.virtualclassroom.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VideoConferenceService {
    
    private final Map<Long, VideoSession> activeSessions = new ConcurrentHashMap<>();
    
    public Map<String, Object> startSession(Long courseId, User instructor, VideoSessionRequest request) {
        VideoSession session = new VideoSession();
        session.setCourseId(courseId);
        session.setInstructor(instructor);
        session.setTitle(request.getTitle());
        session.setDescription(request.getDescription());
        session.setStartTime(LocalDateTime.now());
        session.setActive(true);
        
        activeSessions.put(courseId, session);
        
        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", courseId);
        response.put("status", "started");
        response.put("instructor", instructor.getFullName());
        response.put("title", request.getTitle());
        response.put("startTime", session.getStartTime());
        
        return response;
    }
    
    public Map<String, Object> joinSession(Long courseId, User user, VideoSessionRequest request) {
        VideoSession session = activeSessions.get(courseId);
        
        if (session == null || !session.isActive()) {
            throw new RuntimeException("No active video session for this course");
        }
        
        session.addParticipant(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("action", "user-joined");
        response.put("userId", user.getId());
        response.put("userName", user.getFullName());
        response.put("userRole", user.getRole().name());
        response.put("participantCount", session.getParticipants().size());
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }
    
    public Map<String, Object> leaveSession(Long courseId, User user) {
        VideoSession session = activeSessions.get(courseId);
        
        if (session != null) {
            session.removeParticipant(user);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("action", "user-left");
        response.put("userId", user.getId());
        response.put("userName", user.getFullName());
        response.put("participantCount", session != null ? session.getParticipants().size() : 0);
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }
    
    public Map<String, Object> endSession(Long courseId, User instructor) {
        VideoSession session = activeSessions.remove(courseId);
        
        if (session != null) {
            session.setActive(false);
            session.setEndTime(LocalDateTime.now());
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("action", "session-ended");
        response.put("endedBy", instructor.getFullName());
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }
    
    public Map<String, Object> toggleScreenShare(Long courseId, User user, Map<String, Object> request) {
        VideoSession session = activeSessions.get(courseId);
        
        if (session == null) {
            throw new RuntimeException("No active session");
        }
        
        boolean isSharing = (Boolean) request.getOrDefault("isSharing", false);
        session.setScreenSharing(isSharing);
        session.setScreenShareUser(isSharing ? user : null);
        
        Map<String, Object> response = new HashMap<>();
        response.put("action", "screen-share-toggle");
        response.put("isSharing", isSharing);
        response.put("userId", user.getId());
        response.put("userName", user.getFullName());
        response.put("timestamp", LocalDateTime.now());
        
        return response;
    }
    
    public Map<String, Object> getSessionInfo(Long courseId, User user) {
        VideoSession session = activeSessions.get(courseId);
        
        Map<String, Object> info = new HashMap<>();
        if (session != null && session.isActive()) {
            info.put("active", true);
            info.put("title", session.getTitle());
            info.put("instructor", session.getInstructor().getFullName());
            info.put("participantCount", session.getParticipants().size());
            info.put("startTime", session.getStartTime());
            info.put("isScreenSharing", session.isScreenSharing());
            info.put("screenShareUser", session.getScreenShareUser() != null ? 
                     session.getScreenShareUser().getFullName() : null);
        } else {
            info.put("active", false);
        }
        
        return info;
    }
    
    // Inner class to represent video session
    private static class VideoSession {
        private Long courseId;
        private User instructor;
        private String title;
        private String description;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private boolean active;
        private boolean screenSharing;
        private User screenShareUser;
        private final Map<Long, User> participants = new ConcurrentHashMap<>();
        
        public void addParticipant(User user) {
            participants.put(user.getId(), user);
        }
        
        public void removeParticipant(User user) {
            participants.remove(user.getId());
        }
        
        public Map<Long, User> getParticipants() {
            return participants;
        }
        
        // Getters and setters
        public Long getCourseId() { return courseId; }
        public void setCourseId(Long courseId) { this.courseId = courseId; }
        
        public User getInstructor() { return instructor; }
        public void setInstructor(User instructor) { this.instructor = instructor; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        
        public boolean isScreenSharing() { return screenSharing; }
        public void setScreenSharing(boolean screenSharing) { this.screenSharing = screenSharing; }
        
        public User getScreenShareUser() { return screenShareUser; }
        public void setScreenShareUser(User screenShareUser) { this.screenShareUser = screenShareUser; }
    }
}
