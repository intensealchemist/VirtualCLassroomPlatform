package com.virtualclassroom.dto;

/**
 * DTO for notification messages
 */
public class NotificationMessage {

    public enum NotificationType {
        INFO,
        SUCCESS,
        WARNING,
        ERROR
    }

    private NotificationType type;
    private String title;
    private String message;
    private String targetUrl;
    private long timestamp;

    public NotificationMessage() {
        this.type = NotificationType.INFO;
        this.timestamp = System.currentTimeMillis();
    }

    public NotificationMessage(NotificationType type, String title, String message) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public NotificationMessage(NotificationType type, String title, String message, String targetUrl) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.targetUrl = targetUrl;
        this.timestamp = System.currentTimeMillis();
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}