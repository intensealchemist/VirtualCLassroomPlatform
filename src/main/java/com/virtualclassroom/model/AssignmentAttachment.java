package com.virtualclassroom.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class AssignmentAttachment {
    
    @Column(name = "attachment_name")
    private String name;
    
    @Column(name = "attachment_url")
    private String url;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "attachment_type")
    private ResourceType type;
    
    @Column(name = "attachment_description")
    private String description;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "mime_type")
    private String mimeType;
    
    // Constructors
    public AssignmentAttachment() {}
    
    public AssignmentAttachment(String name, String url, ResourceType type) {
        this.name = name;
        this.url = url;
        this.type = type;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public ResourceType getType() { return type; }
    public void setType(ResourceType type) { this.type = type; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
}
