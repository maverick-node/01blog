package com.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateCommentDTO {
    @NotNull
    private Integer commentId;

    @NotBlank(message = "Content cannot be empty")
    @Size(max = 500, message = "Content too long")
    private String newContent;
}
