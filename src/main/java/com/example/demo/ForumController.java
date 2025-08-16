package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/forums")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ForumController {

    @Autowired
    private DiscussionForumRepository forumRepository;

    @Autowired
    private ForumPostRepository postRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private NotificationService notificationService;

    // Forum Management
    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<DiscussionForum> createForum(@Valid @RequestBody ForumRequest request,
                                                      @AuthenticationPrincipal CustomUserDetails currentUser) {
        Optional<Course> courseOpt = courseRepository.findById(request.getCourseId());
        if (courseOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Course course = courseOpt.get();
        
        // Check if user is instructor of the course or admin
        if (!currentUser.getRole().equals(Role.ADMIN) && 
            !course.getInstructor().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).build();
        }

        DiscussionForum forum = new DiscussionForum(
            request.getTitle(),
            request.getDescription(),
            course,
            currentUser.getUser()
        );

        DiscussionForum savedForum = forumRepository.save(forum);
        return ResponseEntity.ok(savedForum);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<DiscussionForum>> getCourseForums(@PathVariable Long courseId,
                                                               @RequestParam(required = false) String search) {
        List<DiscussionForum> forums;
        
        if (search != null && !search.trim().isEmpty()) {
            forums = forumRepository.findByCourseAndKeyword(courseId, search.trim());
        } else {
            forums = forumRepository.findByCourseIdAndIsActiveTrueOrderByIsPinnedDescCreatedAtDesc(courseId);
        }
        
        return ResponseEntity.ok(forums);
    }

    @GetMapping("/{forumId}")
    public ResponseEntity<DiscussionForum> getForum(@PathVariable Long forumId) {
        Optional<DiscussionForum> forum = forumRepository.findById(forumId);
        return forum.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{forumId}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<DiscussionForum> updateForum(@PathVariable Long forumId,
                                                      @Valid @RequestBody ForumRequest request,
                                                      @AuthenticationPrincipal CustomUserDetails currentUser) {
        Optional<DiscussionForum> forumOpt = forumRepository.findById(forumId);
        if (forumOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DiscussionForum forum = forumOpt.get();
        
        // Check permissions
        if (!currentUser.getRole().equals(Role.ADMIN) && 
            !forum.getCreatedBy().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).build();
        }

        forum.setTitle(request.getTitle());
        forum.setDescription(request.getDescription());
        forum.setUpdatedAt(java.time.LocalDateTime.now());

        DiscussionForum savedForum = forumRepository.save(forum);
        return ResponseEntity.ok(savedForum);
    }

    @PutMapping("/{forumId}/pin")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> togglePin(@PathVariable Long forumId,
                                     @AuthenticationPrincipal CustomUserDetails currentUser) {
        Optional<DiscussionForum> forumOpt = forumRepository.findById(forumId);
        if (forumOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DiscussionForum forum = forumOpt.get();
        
        // Check permissions
        if (!currentUser.getRole().equals(Role.ADMIN) && 
            !forum.getCourse().getInstructor().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).build();
        }

        forum.setPinned(!forum.isPinned());
        forumRepository.save(forum);
        
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{forumId}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteForum(@PathVariable Long forumId,
                                       @AuthenticationPrincipal CustomUserDetails currentUser) {
        Optional<DiscussionForum> forumOpt = forumRepository.findById(forumId);
        if (forumOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DiscussionForum forum = forumOpt.get();
        
        // Check permissions
        if (!currentUser.getRole().equals(Role.ADMIN) && 
            !forum.getCreatedBy().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).build();
        }

        forum.setActive(false);
        forumRepository.save(forum);
        
        return ResponseEntity.ok().build();
    }

    // Post Management
    @PostMapping("/{forumId}/posts")
    public ResponseEntity<ForumPost> createPost(@PathVariable Long forumId,
                                              @Valid @RequestBody PostRequest request,
                                              @AuthenticationPrincipal CustomUserDetails currentUser) {
        Optional<DiscussionForum> forumOpt = forumRepository.findById(forumId);
        if (forumOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        DiscussionForum forum = forumOpt.get();
        ForumPost post = new ForumPost(request.getContent(), forum, currentUser.getUser());

        if (request.getParentPostId() != null) {
            Optional<ForumPost> parentOpt = postRepository.findById(request.getParentPostId());
            if (parentOpt.isPresent()) {
                post.setParentPost(parentOpt.get());
                
                // Notify parent post author
                notificationService.createNotification(
                    parentOpt.get().getAuthor(),
                    "New Reply to Your Post",
                    String.format("%s replied to your post in %s", 
                        currentUser.getUser().getFirstName(), forum.getTitle()),
                    NotificationType.DISCUSSION_REPLY
                );
            }
        }

        ForumPost savedPost = postRepository.save(post);
        return ResponseEntity.ok(savedPost);
    }

    @GetMapping("/{forumId}/posts")
    public ResponseEntity<List<ForumPost>> getForumPosts(@PathVariable Long forumId,
                                                       @RequestParam(required = false) String search) {
        List<ForumPost> posts;
        
        if (search != null && !search.trim().isEmpty()) {
            posts = postRepository.findByForumAndKeyword(forumId, search.trim());
        } else {
            posts = postRepository.findByForumIdAndParentPostIsNullAndIsDeletedFalseOrderByCreatedAtAsc(forumId);
        }
        
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/{postId}/replies")
    public ResponseEntity<List<ForumPost>> getPostReplies(@PathVariable Long postId) {
        List<ForumPost> replies = postRepository.findByParentPostIdAndIsDeletedFalseOrderByCreatedAtAsc(postId);
        return ResponseEntity.ok(replies);
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<ForumPost> updatePost(@PathVariable Long postId,
                                              @Valid @RequestBody PostRequest request,
                                              @AuthenticationPrincipal CustomUserDetails currentUser) {
        Optional<ForumPost> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ForumPost post = postOpt.get();
        
        // Check if user is author or admin
        if (!post.getAuthor().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.status(403).build();
        }

        post.setContent(request.getContent());
        post.setEdited(true);

        ForumPost savedPost = postRepository.save(post);
        return ResponseEntity.ok(savedPost);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId,
                                      @AuthenticationPrincipal CustomUserDetails currentUser) {
        Optional<ForumPost> postOpt = postRepository.findById(postId);
        if (postOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ForumPost post = postOpt.get();
        
        // Check if user is author, course instructor, or admin
        if (!post.getAuthor().getId().equals(currentUser.getId()) && 
            !post.getForum().getCourse().getInstructor().getId().equals(currentUser.getId()) &&
            !currentUser.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.status(403).build();
        }

        post.setDeleted(true);
        postRepository.save(post);
        
        return ResponseEntity.ok().build();
    }
}
