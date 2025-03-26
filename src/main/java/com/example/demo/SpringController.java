package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SpringController {
 
    @Autowired
    private UserRepository userRepo;

    @GetMapping("")
    public String viewHomePage() {
        return "index";
    }
    @GetMapping("/login")
    public String login(Model model) {

        return "login";
    }
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "signup_form";
    }
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> listUsers = userRepo.findAll();
        model.addAttribute("listUsers", listUsers);
        return "users";
    }
    @GetMapping("/start_meeting")
    public String startm(Model model){
    return "start_meeting";
    }
    @GetMapping("/whiteboard")
    public String whiteboard() {
    	return "whiteboard";
    }
    @GetMapping("/home")
    public String homePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
      model.addAttribute("username", userDetails.getUsername());
      return "home"; 
    }
    @PostMapping("/process_register")
    public String processRegistration(User user) {
    	BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
    	String encodedPassword=encoder.encode(user.getPassword());
    	user.setPassword(encodedPassword);
    	userRepo.save(user);

    	return "register_success";
    }
    
    @GetMapping("/upload")
    public String upload() {
    	return "upload";
    }
  
    
}