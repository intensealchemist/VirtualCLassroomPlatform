package com.virtualclassroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for the Virtual Classroom Platform
 * 
 * Enhanced features include:
 * - JWT and OAuth2 authentication
 * - Real-time communication (WebSocket)
 * - File management and sharing
 * - Course and assignment management
 * - Progress tracking and analytics
 * - Mobile-responsive UI
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
public class VirtualClassroomApplication {

    public static void main(String[] args) {
        SpringApplication.run(VirtualClassroomApplication.class, args);
    }
}
