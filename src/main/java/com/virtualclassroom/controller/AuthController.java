package com.virtualclassroom.controller;

import com.virtualclassroom.dto.AuthRequest;
import com.virtualclassroom.dto.AuthResponse;
import com.virtualclassroom.dto.RegisterRequest;
import com.virtualclassroom.dto.PasswordResetRequest;
import com.virtualclassroom.model.Role;
import com.virtualclassroom.model.User;
import com.virtualclassroom.security.JwtUtil;
import com.virtualclassroom.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and user management endpoints")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return JWT token")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(), 
                    authRequest.getPassword()
                )
            );
            
            User user = (User) authentication.getPrincipal();
            String token = jwtUtil.generateToken(authentication);
            String refreshToken = jwtUtil.generateRefreshToken(user);
            
            AuthResponse response = new AuthResponse(
                token, 
                refreshToken,
                "Bearer", 
                jwtUtil.getExpirationTimeInMillis(),
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getFullName()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (AuthenticationException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid credentials");
            error.put("message", "Username or password is incorrect");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
    
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            Role role = Role.valueOf(registerRequest.getRole().toUpperCase());
            
            User user = userService.createUser(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                registerRequest.getFirstName(),
                registerRequest.getLastName(),
                role
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("verificationRequired", true);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid refresh token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        
        if (!jwtUtil.isRefreshToken(refreshToken)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Token is not a refresh token");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        
        try {
            String username = jwtUtil.getUsernameFromToken(refreshToken);
            User user = userService.getUserByUsername(username);
            
            String newToken = jwtUtil.generateTokenForUser(user);
            String newRefreshToken = jwtUtil.generateRefreshToken(user);
            
            AuthResponse response = new AuthResponse(
                newToken,
                newRefreshToken,
                "Bearer",
                jwtUtil.getExpirationTimeInMillis(),
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getFullName()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Token refresh failed");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @GetMapping("/verify-email")
    @Operation(summary = "Verify user email address")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        boolean verified = userService.verifyEmail(token);
        
        Map<String, Object> response = new HashMap<>();
        if (verified) {
            response.put("message", "Email verified successfully");
            response.put("verified", true);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Invalid or expired verification token");
            response.put("verified", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @PostMapping("/forgot-password")
    @Operation(summary = "Initiate password reset process")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        
        if (email == null || email.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Email is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        
        boolean initiated = userService.initiatePasswordReset(email);
        
        Map<String, Object> response = new HashMap<>();
        if (initiated) {
            response.put("message", "Password reset email sent");
            response.put("email", email);
        } else {
            response.put("message", "If the email exists, a reset link has been sent");
        }
        
        // Always return success for security reasons
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/reset-password")
    @Operation(summary = "Reset user password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetRequest resetRequest) {
        boolean reset = userService.resetPassword(resetRequest.getToken(), resetRequest.getNewPassword());
        
        Map<String, Object> response = new HashMap<>();
        if (reset) {
            response.put("message", "Password reset successfully");
            response.put("success", true);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Invalid or expired reset token");
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/me")
    @Operation(summary = "Get current user information")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        User user = (User) authentication.getPrincipal();
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        userInfo.put("firstName", user.getFirstName());
        userInfo.put("lastName", user.getLastName());
        userInfo.put("fullName", user.getFullName());
        userInfo.put("role", user.getRole().name());
        userInfo.put("profilePicture", user.getProfilePicture());
        userInfo.put("bio", user.getBio());
        userInfo.put("phoneNumber", user.getPhoneNumber());
        userInfo.put("isVerified", user.getIsVerified());
        userInfo.put("createdAt", user.getCreatedAt());
        userInfo.put("lastLogin", user.getLastLogin());
        
        return ResponseEntity.ok(userInfo);
    }
    
    @PostMapping("/logout")
    @Operation(summary = "Logout user (invalidate token)")
    public ResponseEntity<?> logout() {
        // In a stateless JWT implementation, logout is handled client-side
        // by removing the token. Server-side token blacklisting can be implemented
        // for enhanced security if needed.
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }
}
