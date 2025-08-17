package com.virtualclassroom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "forum_posts")
public class ForumPost {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Post content is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(name = "is_edited")
    private Boolean isEdited = false;
    
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id", nullable = false)
    @NotNull(message = "Forum is required")
    private DiscussionForum forum;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @NotNull(message = "Author is required")
    private User author;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_post_id")
    private ForumPost parentPost;
    
    @OneToMany(mappedBy = "parentPost", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ForumPost> replies = new HashSet<>();
    
    @ElementCollection
    @CollectionTable(name = "forum_post_attachments", joinColumns = @JoinColumn(name = "post_id"))
    private Set<PostAttachment> attachments = new HashSet<>();
    
    // Constructors
    public ForumPost() {}
    
    public ForumPost(String content, DiscussionForum forum, User author) {
        this.content = content;
        this.forum = forum;
        this.author = author;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper methods
    public boolean isReply() {
        return parentPost != null;
    }
    
    public int getReplyCount() {
        return replies.size();
    }
    
    public void markAsEdited() {
        this.isEdited = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsDeleted() {
        this.isDeleted = true;
        this.content = "[Post deleted]";
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean canBeEditedBy(User user) {
        return author.equals(user) || user.isInstructor() || user.isAdmin();
    }
    
    public boolean canBeDeletedBy(User user) {
        return author.equals(user) || user.isInstructor() || user.isAdmin();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Boolean getIsEdited() { return isEdited; }
    public void setIsEdited(Boolean isEdited) { this.isEdited = isEdited; }
    
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public DiscussionForum getForum() { return forum; }
    public void setForum(DiscussionForum forum) { this.forum = forum; }
    
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    
    public ForumPost getParentPost() { return parentPost; }
    public void setParentPost(ForumPost parentPost) { this.parentPost = parentPost; }
    
    public Set<ForumPost> getReplies() { return replies; }
    public void setReplies(Set<ForumPost> replies) { this.replies = replies; }
    
    public Set<PostAttachment> getAttachments() { return attachments; }
    public void setAttachments(Set<PostAttachment> attachments) { this.attachments = attachments; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ForumPost)) return false;
        ForumPost forumPost = (ForumPost) o;
        return Objects.equals(id, forumPost.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "ForumPost{" +
                "id=" + id +
                ", content='" + (content.length() > 50 ? content.substring(0, 50) + "..." : content) + '\'' +
                ", author=" + (author != null ? author.getUsername() : null) +
                ", createdAt=" + createdAt +
                '}';
    }
}
