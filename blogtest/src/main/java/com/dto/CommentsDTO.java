package com.dto;

import lombok.Data;

@Data
public class CommentsDTO {
    private Integer postId;
    private String comment;
    private String username;
    private java.time.LocalDateTime createdAt;
}
