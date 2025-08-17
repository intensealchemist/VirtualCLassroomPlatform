package com.virtualclassroom.service;

import com.virtualclassroom.dto.WhiteboardAction;
import com.virtualclassroom.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WhiteboardService {
    
    private final Map<Long, WhiteboardSession> whiteboardSessions = new ConcurrentHashMap<>();
    
    public void saveAction(Long courseId, WhiteboardAction action) {
        WhiteboardSession session = whiteboardSessions.computeIfAbsent(courseId, k -> new WhiteboardSession());
        session.addAction(action);
    }
    
    public void clearWhiteboard(Long courseId) {
        WhiteboardSession session = whiteboardSessions.get(courseId);
        if (session != null) {
            session.clearActions();
        }
    }
    
    public boolean hasDrawingPermission(Long courseId, User user) {
        WhiteboardSession session = whiteboardSessions.get(courseId);
        if (session == null) {
            return true; // Default allow drawing
        }
        return session.hasDrawingPermission(user.getId());
    }
    
    public Map<String, Object> setDrawingPermission(Long courseId, Long userId, boolean hasPermission) {
        WhiteboardSession session = whiteboardSessions.computeIfAbsent(courseId, k -> new WhiteboardSession());
        session.setDrawingPermission(userId, hasPermission);
        
        Map<String, Object> response = new HashMap<>();
        response.put("action", "permission-changed");
        response.put("userId", userId);
        response.put("hasPermission", hasPermission);
        response.put("timestamp", System.currentTimeMillis());
        
        return response;
    }
    
    public Map<String, Object> getWhiteboardState(Long courseId, User user) {
        WhiteboardSession session = whiteboardSessions.get(courseId);
        
        Map<String, Object> state = new HashMap<>();
        if (session != null) {
            state.put("actions", session.getActions());
            state.put("hasDrawingPermission", session.hasDrawingPermission(user.getId()));
        } else {
            state.put("actions", new ArrayList<>());
            state.put("hasDrawingPermission", true);
        }
        
        return state;
    }
    
    public Map<String, Object> saveWhiteboardSnapshot(Long courseId, Map<String, Object> request, User user) {
        WhiteboardSession session = whiteboardSessions.get(courseId);
        
        if (session != null) {
            String snapshotData = (String) request.get("snapshotData");
            String title = (String) request.getOrDefault("title", "Whiteboard Snapshot");
            
            WhiteboardSnapshot snapshot = new WhiteboardSnapshot();
            snapshot.setTitle(title);
            snapshot.setData(snapshotData);
            snapshot.setSavedBy(user);
            snapshot.setSavedAt(LocalDateTime.now());
            
            session.addSnapshot(snapshot);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Whiteboard saved successfully");
        response.put("timestamp", System.currentTimeMillis());
        
        return response;
    }
    
    // Inner classes
    private static class WhiteboardSession {
        private final List<WhiteboardAction> actions = new ArrayList<>();
        private final Map<Long, Boolean> drawingPermissions = new ConcurrentHashMap<>();
        private final List<WhiteboardSnapshot> snapshots = new ArrayList<>();
        
        public void addAction(WhiteboardAction action) {
            actions.add(action);
        }
        
        public void clearActions() {
            actions.clear();
        }
        
        public List<WhiteboardAction> getActions() {
            return new ArrayList<>(actions);
        }
        
        public boolean hasDrawingPermission(Long userId) {
            return drawingPermissions.getOrDefault(userId, true);
        }
        
        public void setDrawingPermission(Long userId, boolean hasPermission) {
            drawingPermissions.put(userId, hasPermission);
        }
        
        public void addSnapshot(WhiteboardSnapshot snapshot) {
            snapshots.add(snapshot);
        }
    }
    
    private static class WhiteboardSnapshot {
        private String title;
        private String data;
        private User savedBy;
        private LocalDateTime savedAt;
        
        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
        
        public User getSavedBy() { return savedBy; }
        public void setSavedBy(User savedBy) { this.savedBy = savedBy; }
        
        public LocalDateTime getSavedAt() { return savedAt; }
        public void setSavedAt(LocalDateTime savedAt) { this.savedAt = savedAt; }
    }
}
