package com.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePostDTO {

    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Content is required")
    @NotNull
    private String content;
    @NotBlank(message = "Author is required")
    private String author;
    private String mediaPath;
    private LocalDateTime createdAt = LocalDateTime.now();
}
