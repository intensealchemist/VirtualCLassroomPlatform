package com.virtualclassroom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Value;
import com.virtualclassroom.model.User;
import com.virtualclassroom.model.Role;
import com.virtualclassroom.model.Course;
import com.virtualclassroom.model.CourseLevel;
import com.virtualclassroom.model.CourseStatus;
import com.virtualclassroom.model.CourseCategory;
import com.virtualclassroom.service.UserService;
import com.virtualclassroom.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PageController {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Value("${agora.app-id:${AGORA_APP_ID:}}")
    private String agoraAppId;

    @Value("${agora.channel:${AGORA_CHANNEL:main}}")
    private String agoraChannel;

    @Value("${agora.token:${AGORA_TOKEN:}}")
    private String agoraToken;

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // templates/login.html
    }

    @GetMapping({"/register", "/signup"})
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User()); // must match attribute used in template
        return "signup_form"; // templates/signup_form.html
    }

    @PostMapping("/process_register")
    public String processRegister(@ModelAttribute("user") User formUser, Model model) {
        try {
            // Derive a username if not provided: use email local-part
            String email = formUser.getEmail();
            String username = formUser.getUsername();
            if (username == null || username.isBlank()) {
                if (email != null && email.contains("@")) {
                    username = email.substring(0, email.indexOf('@'));
                } else {
                    username = (formUser.getFirstName() + "." + formUser.getLastName()).toLowerCase().replaceAll("\\s+", "");
                }
            }

            Role role = formUser.getRole() != null ? formUser.getRole() : Role.STUDENT;

            userService.createUser(
                username,
                email,
                formUser.getPassword(),
                formUser.getFirstName(),
                formUser.getLastName(),
                role
            );

            return "register_success"; // templates/register_success.html
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "signup_form";
        }
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("listUsers", users);
        return "users"; // templates/users.html
    }

    @GetMapping("/start_meeting")
    public String startMeetingPage(Model model) {
        // Expose Agora config to the template (values can come from env or application.properties)
        model.addAttribute("appId", (agoraAppId != null && !agoraAppId.isBlank()) ? agoraAppId : null);
        model.addAttribute("channel", (agoraChannel != null && !agoraChannel.isBlank()) ? agoraChannel : "main");
        model.addAttribute("token", (agoraToken != null && !agoraToken.isBlank()) ? agoraToken : null);
        return "start_meeting"; // templates/start_meeting.html
    }

    @GetMapping("/")
    public String root(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        return "redirect:/guest";
    }

    @GetMapping("/index")
    public String indexPage() {
        return "redirect:/";
    }

    @GetMapping("/whiteboard")
    public String whiteboardPage() {
        return "whiteboard"; // templates/whiteboard.html
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        // Redirect to role-specific dashboards
        if (authentication != null) {
            Collection<? extends GrantedAuthority> auths = authentication.getAuthorities();
            boolean isInstructor = auths.stream().anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"));
            boolean isAdmin = auths.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isStudent = auths.stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
            if (isInstructor || isAdmin) return "redirect:/dashboard/instructor";
            if (isStudent) return "redirect:/dashboard/student";
        }
        // Fallback: unauthenticated -> guest
        return "redirect:/guest";
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/dashboard/student")
    public String dashboardStudent(Model model) {
        // Basic sample stats; replace with real data hooking into services as needed
        Map<String, Object> stats = new HashMap<>();
        stats.put("enrolledCourses", 3);
        stats.put("completedLessons", 12);
        stats.put("overallProgress", 42); // percent
        stats.put("currentStreak", 5); // days
        model.addAttribute("stats", stats);

        // Recent activity sample
        List<Map<String, String>> recentActivities = new ArrayList<>();
        Map<String, String> a1 = new HashMap<>();
        a1.put("time", "Today");
        a1.put("description", "Completed Lesson 3 in Go Programming");
        recentActivities.add(a1);
        Map<String, String> a2 = new HashMap<>();
        a2.put("time", "Yesterday");
        a2.put("description", "Scored 85% on IoT Quiz 1");
        recentActivities.add(a2);
        model.addAttribute("recentActivities", recentActivities);

        // Enrolled courses sample
        List<Map<String, Object>> courses = new ArrayList<>();
        Map<String, Object> c1 = new HashMap<>();
        c1.put("id", 1);
        c1.put("name", "Go Programming");
        c1.put("description", "Master the Go language with hands-on projects.");
        c1.put("progress", 60);
        courses.add(c1);
        Map<String, Object> c2 = new HashMap<>();
        c2.put("id", 2);
        c2.put("name", "Internet of Things (IoT)");
        c2.put("description", "Explore sensors, connectivity, and platforms.");
        c2.put("progress", 35);
        courses.add(c2);
        model.addAttribute("courses", courses);

        return "dashboard_student"; // templates/dashboard_student.html
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @GetMapping("/dashboard/instructor")
    public String dashboardInstructor(Model model) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("coursesTaught", 4);
        stats.put("activeStudents", 128);
        stats.put("assignmentsToGrade", 9);
        stats.put("upcomingSessions", 2);
        model.addAttribute("stats", stats);

        List<Map<String, String>> recentActivities = new ArrayList<>();
        Map<String, String> a1 = new HashMap<>();
        a1.put("time", "Today");
        a1.put("description", "Published Lesson 5 in Android Programming");
        recentActivities.add(a1);
        Map<String, String> a2 = new HashMap<>();
        a2.put("time", "Today");
        a2.put("description", "Reviewed 12 submissions for IoT Assignment 2");
        recentActivities.add(a2);
        model.addAttribute("recentActivities", recentActivities);

        List<Map<String, Object>> courses = new ArrayList<>();
        Map<String, Object> c1 = new HashMap<>();
        c1.put("id", 101);
        c1.put("name", "Android Programming");
        c1.put("description", "Kotlin, Jetpack, and modern Android development.");
        c1.put("progress", 80);
        courses.add(c1);
        Map<String, Object> c2 = new HashMap<>();
        c2.put("id", 102);
        c2.put("name", "Software Project Management");
        c2.put("description", "Agile, Scrum, estimation, and delivery.");
        c2.put("progress", 55);
        courses.add(c2);
        model.addAttribute("courses", courses);

        return "dashboard_instructor"; // templates/dashboard_instructor.html
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/guest")
    public String guestPage(Authentication authentication) {
        // If somehow authenticated, redirect to the main dashboard
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        return "guest"; // templates/guest.html
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @GetMapping("/instructor/courses/create")
    public String showCreateCourseForm(Model model) {
        // Load categories from DB and provide levels
        List<?> categories = courseService.getActiveCategories();
        List<String> levels = Arrays.asList("Beginner", "Intermediate", "Advanced");
        model.addAttribute("categories", categories);
        model.addAttribute("levels", levels);
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new HashMap<String, Object>());
        }
        return "create_course"; // templates/create_course.html
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @PostMapping("/instructor/courses/create")
    public String handleCreateCourse(
            @RequestParam Map<String, String> form,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {
        String title = form.getOrDefault("name", "").trim();
        String description = form.getOrDefault("description", "").trim();
        String levelStr = form.getOrDefault("level", "");
        String priceStr = form.getOrDefault("price", "");
        String categoryName = form.getOrDefault("category", "");
        boolean publish = "true".equalsIgnoreCase(form.getOrDefault("published", "false"));

        Map<String, String> errors = new HashMap<>();
        if (title.isBlank()) errors.put("name", "Course title is required");
        if (description.isBlank()) errors.put("description", "Description is required");
        // Validate level
        CourseLevel parsedLevel = CourseLevel.BEGINNER;
        if (!levelStr.isBlank()) {
            try { parsedLevel = CourseLevel.valueOf(levelStr.toUpperCase()); } catch (IllegalArgumentException ex) {
                errors.put("level", "Invalid level selected");
            }
        } else {
            errors.put("level", "Please select a level");
        }
        // Validate price
        java.math.BigDecimal parsedPrice = null;
        if (!priceStr.isBlank()) {
            try { parsedPrice = new java.math.BigDecimal(priceStr); if (parsedPrice.signum() < 0) errors.put("price", "Price cannot be negative"); }
            catch (NumberFormatException ex) { errors.put("price", "Invalid price format"); }
        }

        // Resolve category (optional but if provided must exist)
        CourseCategory resolvedCategory = null;
        if (!categoryName.isBlank()) {
            resolvedCategory = courseService.findActiveCategoryByNameIgnoreCase(categoryName).orElse(null);
            if (resolvedCategory == null) {
                errors.put("category", "Selected category is not available");
            }
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("form", form);
            model.addAttribute("categories", courseService.getActiveCategories());
            model.addAttribute("levels", Arrays.asList("Beginner", "Intermediate", "Advanced"));
            return "create_course";
        }

        // Resolve instructor from authentication
        User instructor = userService.getUserByUsername(authentication.getName());

        // Generate a simple unique course code based on title + time
        String base = title.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        if (base.length() > 8) base = base.substring(0, 8);
        String courseCode = base + "-" + (System.currentTimeMillis() % 100000);

        // Create draft course with optional category
        Course course = courseService.createCourse(title, courseCode, description, instructor, resolvedCategory);
        course.setLevel(parsedLevel);
        if (parsedPrice != null) course.setPrice(parsedPrice);
        course.setIsPublic(Boolean.TRUE);
        course.setStatus(publish ? CourseStatus.PUBLISHED : CourseStatus.DRAFT);
        courseService.save(course);

        redirectAttributes.addFlashAttribute("message", "Course '" + title + "' created successfully.");
        return "redirect:/dashboard/instructor";
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    @GetMapping("/instructor/courses/{id}/edit")
    public String showEditCourse(
            @PathVariable("id") Long id,
            Authentication authentication,
            Model model) {
        // Derive current user safely from Authentication. Avoid throwing if username not found.
        User current = null;
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            current = (User) principal;
        } else {
            try {
                current = userService.getUserByUsername(authentication.getName());
            } catch (RuntimeException ex) {
                return "redirect:/login";
            }
        }
        Course course = courseService.getCourseById(id);
        // Simple permission check mirroring service's canEdit logic
        if (!(current.isAdmin() || course.getInstructor().equals(current))) {
            // Avoid leaking existence; redirect to dashboard
            return "redirect:/dashboard/instructor";
        }
        model.addAttribute("course", course);
        model.addAttribute("categories", courseService.getActiveCategories());
        model.addAttribute("levels", java.util.Arrays.asList("Beginner", "Intermediate", "Advanced"));
        return "edit_course"; // templates/edit_course.html
    }
}
