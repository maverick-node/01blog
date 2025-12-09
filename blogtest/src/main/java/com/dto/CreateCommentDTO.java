package com.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CreateCommentDTO {
    private Integer postId;
    private String content;
    private LocalDateTime createdAt = LocalDateTime.now();
}
