package com.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public class PostDTO {

    private Integer id;
    @Size(max = 50, message = "Title cannot exceed 100 characters")
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String author;
    private boolean hidden = false; 

    // MULTIPLE MEDIA SUPPORT
    private List<String> mediaPaths = new ArrayList<>();
    private List<String> mediaTypes = new ArrayList<>();
    private List<Integer> mediaIds = new ArrayList<>();

    // === GETTERS & SETTERS (MUST HAVE THESE) ===
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    // MEDIA GETTERS & SETTERS
    public List<String> getMediaPaths() { return mediaPaths; }
    public void setMediaPaths(List<String> mediaPaths) { 
        this.mediaPaths = mediaPaths != null ? mediaPaths : new ArrayList<>(); 
    }

    public List<String> getMediaTypes() { return mediaTypes; }
    public void setMediaTypes(List<String> mediaTypes) { 
        this.mediaTypes = mediaTypes != null ? mediaTypes : new ArrayList<>(); 
    }

    public List<Integer> getMediaIds() { return mediaIds; }
    public void setMediaIds(List<Integer> mediaIds) { 
        this.mediaIds = mediaIds != null ? mediaIds : new ArrayList<>(); 
    }
     public boolean isHidden() { return hidden; }
    public void setHidden(boolean hidden) { this.hidden = hidden; }
}