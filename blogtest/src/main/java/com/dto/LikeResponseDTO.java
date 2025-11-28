package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeResponseDTO {
    private Integer postId;
    private Integer userId;
    private Boolean liked;
}
