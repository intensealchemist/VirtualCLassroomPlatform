package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardStats> getAdminStats() {
        DashboardStats stats = dashboardService.getAdminDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/student")
    public ResponseEntity<StudentDashboard> getStudentDashboard(@AuthenticationPrincipal CustomUserDetails currentUser) {
        if (!currentUser.getRole().equals(Role.STUDENT)) {
            return ResponseEntity.status(403).build();
        }
        
        StudentDashboard dashboard = dashboardService.getStudentDashboard(currentUser.getId());
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/instructor")
    public ResponseEntity<InstructorDashboard> getInstructorDashboard(@AuthenticationPrincipal CustomUserDetails currentUser) {
        if (!currentUser.getRole().equals(Role.INSTRUCTOR) && !currentUser.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.status(403).build();
        }
        
        InstructorDashboard dashboard = dashboardService.getInstructorDashboard(currentUser.getId());
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<StudentDashboard> getStudentDashboardById(@PathVariable Long studentId) {
        StudentDashboard dashboard = dashboardService.getStudentDashboard(studentId);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/instructor/{instructorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InstructorDashboard> getInstructorDashboardById(@PathVariable Long instructorId) {
        InstructorDashboard dashboard = dashboardService.getInstructorDashboard(instructorId);
        return ResponseEntity.ok(dashboard);
    }
}
