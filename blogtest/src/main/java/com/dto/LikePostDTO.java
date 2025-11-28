package com.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LikePostDTO {
    @NotNull
    private Integer postId;
}

