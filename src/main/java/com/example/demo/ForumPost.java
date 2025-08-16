package com.example.demo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "forum_posts")
public class ForumPost {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Post content is required")
    @Size(max = 2000, message = "Post content cannot exceed 2000 characters")
    @Column(name = "content", nullable = false, length = 2000)
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id", nullable = false)
    private DiscussionForum forum;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_post_id")
    private ForumPost parentPost;
    
    @OneToMany(mappedBy = "parentPost", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ForumPost> replies;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "is_edited")
    private boolean isEdited = false;
    
    @Column(name = "likes_count")
    private int likesCount = 0;
    
    @Column(name = "is_deleted")
    private boolean isDeleted = false;
    
    // Constructors
    public ForumPost() {}
    
    public ForumPost(String content, DiscussionForum forum, User author) {
        this.content = content;
        this.forum = forum;
        this.author = author;
    }
    
    public ForumPost(String content, DiscussionForum forum, User author, ForumPost parentPost) {
        this.content = content;
        this.forum = forum;
        this.author = author;
        this.parentPost = parentPost;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public DiscussionForum getForum() {
        return forum;
    }
    
    public void setForum(DiscussionForum forum) {
        this.forum = forum;
    }
    
    public User getAuthor() {
        return author;
    }
    
    public void setAuthor(User author) {
        this.author = author;
    }
    
    public ForumPost getParentPost() {
        return parentPost;
    }
    
    public void setParentPost(ForumPost parentPost) {
        this.parentPost = parentPost;
    }
    
    public List<ForumPost> getReplies() {
        return replies;
    }
    
    public void setReplies(List<ForumPost> replies) {
        this.replies = replies;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public boolean isEdited() {
        return isEdited;
    }
    
    public void setEdited(boolean edited) {
        isEdited = edited;
        if (edited) {
            updatedAt = LocalDateTime.now();
        }
    }
    
    public int getLikesCount() {
        return likesCount;
    }
    
    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }
    
    public boolean isDeleted() {
        return isDeleted;
    }
    
    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
