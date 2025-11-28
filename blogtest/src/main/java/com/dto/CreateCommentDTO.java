package com.dto;

import lombok.Data;

@Data
public class CreateCommentDTO {
    private Integer postId;
    private String content;
}
