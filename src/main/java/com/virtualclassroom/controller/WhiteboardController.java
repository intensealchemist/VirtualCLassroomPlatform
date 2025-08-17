package com.virtualclassroom.controller;

import com.virtualclassroom.dto.WhiteboardAction;
import com.virtualclassroom.model.Course;
import com.virtualclassroom.model.User;
import com.virtualclassroom.service.CourseService;
import com.virtualclassroom.service.WhiteboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@Controller
public class WhiteboardController {
    
    @Autowired
    private WhiteboardService whiteboardService;
    
    @Autowired
    private CourseService courseService;
    
    @MessageMapping("/whiteboard.draw/{courseId}")
    @SendTo("/topic/whiteboard/{courseId}")
    public WhiteboardAction drawOnWhiteboard(@DestinationVariable Long courseId,
                                           @Payload WhiteboardAction action,
                                           Principal principal) {
        
        User user = (User) ((Authentication) principal).getPrincipal();
        Course course = courseService.getCourseById(courseId);
        
        // Verify access
        if (!courseService.isStudentEnrolled(user, course) && 
            !course.getInstructor().equals(user) && 
            !user.isAdmin()) {
            throw new RuntimeException("Not authorized to use whiteboard");
        }
        
        // Check if user has drawing permission
        if (!whiteboardService.hasDrawingPermission(courseId, user)) {
            throw new RuntimeException("No drawing permission");
        }
        
        action.setUserId(user.getId());
        action.setUserName(user.getFullName());
        action.setTimestamp(System.currentTimeMillis());
        
        whiteboardService.saveAction(courseId, action);
        
        return action;
    }
    
    @MessageMapping("/whiteboard.clear/{courseId}")
    @SendTo("/topic/whiteboard/{courseId}")
    public WhiteboardAction clearWhiteboard(@DestinationVariable Long courseId,
                                          Principal principal) {
        
        User user = (User) ((Authentication) principal).getPrincipal();
        Course course = courseService.getCourseById(courseId);
        
        // Only instructors can clear whiteboard
        if (!course.getInstructor().equals(user) && !user.isAdmin()) {
            throw new RuntimeException("Only instructors can clear whiteboard");
        }
        
        WhiteboardAction clearAction = new WhiteboardAction();
        clearAction.setAction("clear");
        clearAction.setUserId(user.getId());
        clearAction.setUserName(user.getFullName());
        clearAction.setTimestamp(System.currentTimeMillis());
        
        whiteboardService.clearWhiteboard(courseId);
        
        return clearAction;
    }
    
    @MessageMapping("/whiteboard.permission/{courseId}")
    @SendTo("/topic/whiteboard/{courseId}/permission")
    public Map<String, Object> toggleDrawingPermission(@DestinationVariable Long courseId,
                                                      @Payload Map<String, Object> request,
                                                      Principal principal) {
        
        User instructor = (User) ((Authentication) principal).getPrincipal();
        Course course = courseService.getCourseById(courseId);
        
        // Only instructors can manage permissions
        if (!course.getInstructor().equals(instructor) && !instructor.isAdmin()) {
            throw new RuntimeException("Only instructors can manage whiteboard permissions");
        }
        
        Long studentId = Long.valueOf(request.get("studentId").toString());
        boolean hasPermission = (Boolean) request.get("hasPermission");
        
        return whiteboardService.setDrawingPermission(courseId, studentId, hasPermission);
    }
    
    @GetMapping("/api/whiteboard/{courseId}/state")
    @ResponseBody
    public Map<String, Object> getWhiteboardState(@PathVariable Long courseId,
                                                 Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        Course course = courseService.getCourseById(courseId);
        
        // Verify access
        if (!courseService.isStudentEnrolled(user, course) && 
            !course.getInstructor().equals(user) && 
            !user.isAdmin()) {
            throw new RuntimeException("Not authorized to view whiteboard");
        }
        
        return whiteboardService.getWhiteboardState(courseId, user);
    }
    
    @PostMapping("/api/whiteboard/{courseId}/save")
    @ResponseBody
    public Map<String, Object> saveWhiteboard(@PathVariable Long courseId,
                                            @RequestBody Map<String, Object> request,
                                            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        Course course = courseService.getCourseById(courseId);
        
        // Only instructors can save whiteboard
        if (!course.getInstructor().equals(user) && !user.isAdmin()) {
            throw new RuntimeException("Only instructors can save whiteboard");
        }
        
        return whiteboardService.saveWhiteboardSnapshot(courseId, request, user);
    }
}
