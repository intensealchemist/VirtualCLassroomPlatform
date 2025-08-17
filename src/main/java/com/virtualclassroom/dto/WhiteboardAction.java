package com.virtualclassroom.dto;

public class WhiteboardAction {
    
    private String action; // draw, erase, clear, move, etc.
    private String tool; // pen, eraser, line, rectangle, circle, text
    private String color;
    private int strokeWidth;
    private double startX;
    private double startY;
    private double endX;
    private double endY;
    private String text;
    private Long userId;
    private String userName;
    private long timestamp;
    
    // Constructors
    public WhiteboardAction() {}
    
    public WhiteboardAction(String action, String tool) {
        this.action = action;
        this.tool = tool;
    }
    
    // Getters and Setters
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public String getTool() { return tool; }
    public void setTool(String tool) { this.tool = tool; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public int getStrokeWidth() { return strokeWidth; }
    public void setStrokeWidth(int strokeWidth) { this.strokeWidth = strokeWidth; }
    
    public double getStartX() { return startX; }
    public void setStartX(double startX) { this.startX = startX; }
    
    public double getStartY() { return startY; }
    public void setStartY(double startY) { this.startY = startY; }
    
    public double getEndX() { return endX; }
    public void setEndX(double endX) { this.endX = endX; }
    
    public double getEndY() { return endY; }
    public void setEndY(double endY) { this.endY = endY; }
    
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
