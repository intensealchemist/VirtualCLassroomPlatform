package com.example.demo;

public class FileUploadResponse {
    
    private String originalName;
    private String storedName;
    private Long fileSize;
    private String downloadUrl;
    
    public FileUploadResponse() {}
    
    public FileUploadResponse(String originalName, String storedName, Long fileSize, String downloadUrl) {
        this.originalName = originalName;
        this.storedName = storedName;
        this.fileSize = fileSize;
        this.downloadUrl = downloadUrl;
    }
    
    public String getOriginalName() {
        return originalName;
    }
    
    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }
    
    public String getStoredName() {
        return storedName;
    }
    
    public void setStoredName(String storedName) {
        this.storedName = storedName;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getDownloadUrl() {
        return downloadUrl;
    }
    
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
