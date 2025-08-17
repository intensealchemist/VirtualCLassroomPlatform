package com.virtualclassroom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import com.virtualclassroom.model.User;
import com.virtualclassroom.model.Role;
import com.virtualclassroom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;

@Controller
public class PageController {

    @Autowired
    private UserService userService;

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
    public String startMeetingPage() {
        return "start_meeting"; // templates/start_meeting.html
    }

    @GetMapping("/index")
    public String indexPage() {
        return "index"; // templates/index.html
    }

    @GetMapping("/whiteboard")
    public String whiteboardPage() {
        return "whiteboard"; // templates/whiteboard.html
    }
}
