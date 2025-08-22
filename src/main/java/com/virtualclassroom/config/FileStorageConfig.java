package com.virtualclassroom.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Configuration for file storage operations
 * Handles uploading, retrieving, and deleting files
 */
@Configuration
public class FileStorageConfig {

    @Value("${file.upload-dir}")
    private String uploadDir;
    
    private Path rootLocation;
    
    @Bean
    public Path initStorage() {
        try {
            rootLocation = Paths.get(uploadDir);
            Files.createDirectories(rootLocation);
            
            // Create subdirectories for different file types
            Files.createDirectories(rootLocation.resolve("assignments"));
            Files.createDirectories(rootLocation.resolve("submissions"));
            Files.createDirectories(rootLocation.resolve("resources"));
            Files.createDirectories(rootLocation.resolve("profiles"));
            
            System.out.println("File storage initialized at: " + rootLocation.toAbsolutePath());
            return rootLocation;
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize file storage", e);
        }
    }
    
    public String store(MultipartFile file, String subdirectory) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file");
            }
            
            // Generate a unique filename to prevent collisions
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;
            
            // Create the target directory if it doesn't exist
            Path targetDir = rootLocation.resolve(subdirectory);
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            
            // Copy the file to the target location
            Path destinationFile = targetDir.resolve(filename);
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            
            return subdirectory + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
    
    public Resource loadAsResource(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }
    
    public void deleteFile(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file: " + filename, e);
        }
    }
    
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
}