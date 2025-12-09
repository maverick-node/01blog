package com.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "posts")
@ToString(exclude = { "likes", "comments", "reports", "mediaFiles" })
public class PostsStruct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 10000)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Author
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author", nullable = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private UserStruct authorUser;

    // Likes
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikesStruct> likes = new ArrayList<>();

    // Comments
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentStruct> comments = new ArrayList<>();

    // Reports
    @OneToMany(mappedBy = "reportedPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportStruct> reports = new ArrayList<>();

    // MULTIPLE MEDIA FILES
    @OneToMany(
        mappedBy = "post",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private List<FileStruct> mediaFiles = new ArrayList<>();

    // HELPER METHODS – keep your frontend working!
    public List<String> getMediaPaths() {
        return mediaFiles != null
            ? mediaFiles.stream()
                        .map(FileStruct::getFilePath)
                        .toList()
            : List.of();
    }

    public List<String> getMediaTypes() {
        return mediaFiles != null
            ? mediaFiles.stream()
                        .map(FileStruct::getFileType)
                        .toList()
            : List.of();
    }

    public List<Integer> getMediaIds() {
        return mediaFiles != null
            ? mediaFiles.stream()
                        .map(FileStruct::getId)
                        .toList()
            : List.of();
    }

    // Helper to add file (maintains both sides of relationship)
    public void addMediaFile(FileStruct file) {
        if (mediaFiles == null) mediaFiles = new ArrayList<>();
        mediaFiles.add(file);
        file.setPost(this);
    }

    // Helper to remove file
    public void removeMediaFile(FileStruct file) {
        if (mediaFiles != null) {
            mediaFiles.remove(file);
            file.setPost(null);
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}