package com.virtualclassroom.dto;

import jakarta.validation.constraints.NotBlank;

public class VideoSessionRequest {
    
    @NotBlank(message = "Session title is required")
    private String title;
    
    private String description;
    
    private boolean recordSession = false;
    
    private boolean allowStudentVideo = true;
    
    private boolean allowStudentAudio = true;
    
    private boolean allowScreenShare = false;
    
    private int maxParticipants = 50;
    
    // Constructors
    public VideoSessionRequest() {}
    
    public VideoSessionRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }
    
    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public boolean isRecordSession() { return recordSession; }
    public void setRecordSession(boolean recordSession) { this.recordSession = recordSession; }
    
    public boolean isAllowStudentVideo() { return allowStudentVideo; }
    public void setAllowStudentVideo(boolean allowStudentVideo) { this.allowStudentVideo = allowStudentVideo; }
    
    public boolean isAllowStudentAudio() { return allowStudentAudio; }
    public void setAllowStudentAudio(boolean allowStudentAudio) { this.allowStudentAudio = allowStudentAudio; }
    
    public boolean isAllowScreenShare() { return allowScreenShare; }
    public void setAllowScreenShare(boolean allowScreenShare) { this.allowScreenShare = allowScreenShare; }
    
    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }
}
