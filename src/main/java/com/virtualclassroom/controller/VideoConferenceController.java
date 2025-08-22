package com.virtualclassroom.controller;

import com.virtualclassroom.dto.VideoSessionRequest;
import com.virtualclassroom.model.Course;
import com.virtualclassroom.model.User;
import com.virtualclassroom.service.CourseService;
import com.virtualclassroom.service.VideoConferenceService;
import com.virtualclassroom.service.AgoraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.Map;

@Controller
public class VideoConferenceController {
    
    @Autowired
    private VideoConferenceService videoConferenceService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private AgoraService agoraService;
    
    @MessageMapping("/video.join/{courseId}")
    @SendTo("/topic/video/{courseId}")
    public Map<String, Object> joinVideoSession(@DestinationVariable Long courseId,
                                              @Payload VideoSessionRequest request,
                                              Principal principal) {
        
        User user = (User) ((Authentication) principal).getPrincipal();
        Course course = courseService.getCourseById(courseId);
        
        // Verify access
        if (!courseService.isStudentEnrolled(user, course) && 
            !course.getInstructor().equals(user) && 
            !user.isAdmin()) {
            throw new RuntimeException("Not authorized to join video session");
        }
        
        return videoConferenceService.joinSession(courseId, user, request);
    }
    
    @MessageMapping("/video.leave/{courseId}")
    @SendTo("/topic/video/{courseId}")
    public Map<String, Object> leaveVideoSession(@DestinationVariable Long courseId,
                                               Principal principal) {
        
        User user = (User) ((Authentication) principal).getPrincipal();
        return videoConferenceService.leaveSession(courseId, user);
    }
    
    @MessageMapping("/video.offer/{courseId}")
    public void sendOffer(@DestinationVariable Long courseId,
                         @Payload Map<String, Object> offer,
                         Principal principal) {
        
        User sender = (User) ((Authentication) principal).getPrincipal();
        String targetUserId = (String) offer.get("targetUserId");
        
        messagingTemplate.convertAndSendToUser(
            targetUserId,
            "/queue/video/offer",
            Map.of(
                "offer", offer.get("offer"),
                "senderId", sender.getId().toString(),
                "senderName", sender.getFullName(),
                "courseId", courseId
            )
        );
    }
    
    @MessageMapping("/video.answer/{courseId}")
    public void sendAnswer(@DestinationVariable Long courseId,
                          @Payload Map<String, Object> answer,
                          Principal principal) {
        
        User sender = (User) ((Authentication) principal).getPrincipal();
        String targetUserId = (String) answer.get("targetUserId");
        
        messagingTemplate.convertAndSendToUser(
            targetUserId,
            "/queue/video/answer",
            Map.of(
                "answer", answer.get("answer"),
                "senderId", sender.getId().toString(),
                "courseId", courseId
            )
        );
    }
    
    @MessageMapping("/video.ice-candidate/{courseId}")
    public void sendIceCandidate(@DestinationVariable Long courseId,
                                @Payload Map<String, Object> candidate,
                                Principal principal) {
        
        User sender = (User) ((Authentication) principal).getPrincipal();
        String targetUserId = (String) candidate.get("targetUserId");
        
        messagingTemplate.convertAndSendToUser(
            targetUserId,
            "/queue/video/ice-candidate",
            Map.of(
                "candidate", candidate.get("candidate"),
                "senderId", sender.getId().toString(),
                "courseId", courseId
            )
        );
    }
    
    @MessageMapping("/video.screen-share/{courseId}")
    @SendTo("/topic/video/{courseId}/screen-share")
    public Map<String, Object> toggleScreenShare(@DestinationVariable Long courseId,
                                                @Payload Map<String, Object> request,
                                                Principal principal) {
        
        User user = (User) ((Authentication) principal).getPrincipal();
        Course course = courseService.getCourseById(courseId);
        
        // Only instructors can share screen by default
        if (!course.getInstructor().equals(user) && !user.isAdmin()) {
            throw new RuntimeException("Only instructors can share screen");
        }
        
        return videoConferenceService.toggleScreenShare(courseId, user, request);
    }
    
    @GetMapping("/api/video/session/{courseId}")
    @ResponseBody
    public Map<String, Object> getSessionInfo(@PathVariable Long courseId,
                                            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        return videoConferenceService.getSessionInfo(courseId, user);
    }
    
    @PostMapping("/api/video/session/{courseId}/start")
    @ResponseBody
    public Map<String, Object> startVideoSession(@PathVariable Long courseId,
                                               @RequestBody VideoSessionRequest request,
                                               Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        Course course = courseService.getCourseById(courseId);
        
        // Only instructors can start sessions
        if (!course.getInstructor().equals(user) && !user.isAdmin()) {
            throw new RuntimeException("Only instructors can start video sessions");
        }
        
        return videoConferenceService.startSession(courseId, user, request);
    }
    
    @PostMapping("/api/video/session/{courseId}/end")
    @ResponseBody
    public Map<String, Object> endVideoSession(@PathVariable Long courseId,
                                             Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        Course course = courseService.getCourseById(courseId);
        
        // Only instructors can end sessions
        if (!course.getInstructor().equals(user) && !user.isAdmin()) {
            throw new RuntimeException("Only instructors can end video sessions");
        }
        
        return videoConferenceService.endSession(courseId, user);
    }

    // Token refresh endpoint used by the frontend when token is about to expire
    @GetMapping("/api/video/token/refresh")
    @ResponseBody
    public Map<String, Object> refreshAgoraToken(@RequestParam("channel") String channel,
                                                 Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        int uid = user.getId().intValue();
        String newToken = agoraService.generateRtcTokenOrNull(channel, String.valueOf(uid), "publisher");
        return Map.of(
                "token", newToken,
                "expiresAt", agoraService.getExpiryTimestamp()
        );
    }

    // Render the start meeting page with Agora configuration in the model
    @GetMapping("/meeting/{courseId}")
    public String renderMeetingPage(@PathVariable Long courseId,
                                    @RequestParam(value = "channel", required = false) String channel,
                                    Authentication authentication,
                                    Model model) {
        // Resolve course for basic validation (optional)
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            throw new RuntimeException("Course not found");
        }

        String resolvedChannel = (channel != null && !channel.isBlank()) ? channel : ("course-" + courseId);

        // Numeric UID bound to current user (recommended when generating tokens with uid)
        int uid = (authentication != null && authentication.getPrincipal() instanceof User)
                ? ((User) authentication.getPrincipal()).getId().intValue()
                : 0;

        String token = agoraService.generateRtcTokenOrNull(resolvedChannel, String.valueOf(uid), "publisher");

        model.addAttribute("appId", agoraService.getAppId());
        model.addAttribute("channel", resolvedChannel);
        model.addAttribute("token", token);
        model.addAttribute("uid", uid);

        return "start_meeting";
    }

    
}
