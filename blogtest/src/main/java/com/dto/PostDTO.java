package com.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {

    private Integer id;

    @Size(max = 50, message = "Title cannot exceed 50 characters")
    private String title;

    private String content;

    private LocalDateTime createdAt;

    private String author;

    @Builder.Default
    private boolean hidden = false;

    
    @Builder.Default
    private List<String> mediaPaths = new ArrayList<>();

    @Builder.Default
    private List<String> mediaTypes = new ArrayList<>();

    @Builder.Default
    private List<Integer> mediaIds = new ArrayList<>();
}
