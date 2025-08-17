package com.virtualclassroom.service;

import com.virtualclassroom.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    @Value("${app.mail.from:noreply@virtualclassroom.com}")
    private String fromEmail;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;
    
    public void sendVerificationEmail(User user) {
        if (!mailEnabled) {
            // Email disabled in this environment; do nothing
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Verify Your Email - Virtual Classroom");
            
            Context context = new Context();
            context.setVariable("user", user);
            context.setVariable("verificationUrl", baseUrl + "/auth/verify-email?token=" + user.getVerificationToken());
            
            String htmlContent = templateEngine.process("email/verification", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (Exception e) {
            // Fallback to simple text email
            sendSimpleVerificationEmail(user);
        }
    }
    
    public void sendPasswordResetEmail(User user, String resetToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Password Reset - Virtual Classroom");
            
            Context context = new Context();
            context.setVariable("user", user);
            context.setVariable("resetUrl", baseUrl + "/auth/reset-password?token=" + resetToken);
            
            String htmlContent = templateEngine.process("email/password-reset", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (Exception e) {
            // Fallback to simple text email
            sendSimplePasswordResetEmail(user, resetToken);
        }
    }
    
    public void sendWelcomeEmail(User user) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Welcome to Virtual Classroom!");
            
            Context context = new Context();
            context.setVariable("user", user);
            context.setVariable("dashboardUrl", baseUrl + "/dashboard");
            
            String htmlContent = templateEngine.process("email/welcome", context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (Exception e) {
            // Fallback to simple text email
            sendSimpleWelcomeEmail(user);
        }
    }
    
    public void sendCourseEnrollmentEmail(User user, String courseName) {
        if (!mailEnabled) {
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Course Enrollment Confirmation - " + courseName);
            message.setText(String.format(
                "Dear %s,\n\n" +
                "You have successfully enrolled in the course: %s\n\n" +
                "You can access your course at: %s/dashboard\n\n" +
                "Best regards,\n" +
                "Virtual Classroom Team",
                user.getFullName(), courseName, baseUrl
            ));
            mailSender.send(message);
        } catch (Exception ignored) { }
    }
    
    public void sendAssignmentNotificationEmail(User user, String assignmentTitle, String courseName) {
        if (!mailEnabled) {
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("New Assignment: " + assignmentTitle);
            message.setText(String.format(
                "Dear %s,\n\n" +
                "A new assignment has been posted in your course: %s\n\n" +
                "Assignment: %s\n\n" +
                "Please log in to view the details and submit your work.\n\n" +
                "Access your dashboard: %s/dashboard\n\n" +
                "Best regards,\n" +
                "Virtual Classroom Team",
                user.getFullName(), courseName, assignmentTitle, baseUrl
            ));
            mailSender.send(message);
        } catch (Exception ignored) { }
    }
    
    private void sendSimpleVerificationEmail(User user) {
        if (!mailEnabled) {
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Verify Your Email - Virtual Classroom");
            message.setText(String.format(
                "Dear %s,\n\n" +
                "Please verify your email address by clicking the link below:\n\n" +
                "%s/auth/verify-email?token=%s\n\n" +
                "If you didn't create an account, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Virtual Classroom Team",
                user.getFullName(), baseUrl, user.getVerificationToken()
            ));
            mailSender.send(message);
        } catch (Exception ignored) { }
    }
    
    private void sendSimplePasswordResetEmail(User user, String resetToken) {
        if (!mailEnabled) {
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Password Reset - Virtual Classroom");
            message.setText(String.format(
                "Dear %s,\n\n" +
                "You requested a password reset. Click the link below to reset your password:\n\n" +
                "%s/auth/reset-password?token=%s\n\n" +
                "This link will expire in 24 hours.\n\n" +
                "If you didn't request this reset, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Virtual Classroom Team",
                user.getFullName(), baseUrl, resetToken
            ));
            mailSender.send(message);
        } catch (Exception ignored) { }
    }
    
    private void sendSimpleWelcomeEmail(User user) {
        if (!mailEnabled) {
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Welcome to Virtual Classroom!");
            message.setText(String.format(
                "Dear %s,\n\n" +
                "Welcome to Virtual Classroom! Your account has been successfully created.\n\n" +
                "You can now access your dashboard at: %s/dashboard\n\n" +
                "Start exploring courses and begin your learning journey!\n\n" +
                "Best regards,\n" +
                "Virtual Classroom Team",
                user.getFullName(), baseUrl
            ));
            mailSender.send(message);
        } catch (Exception ignored) { }
    }
}
