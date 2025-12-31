package com.controller.posts;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dto.CreatePostDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.PostService;

import jakarta.validation.Valid;

@RestController

public class CreatePost {

    private final PostService postService;

    public CreatePost(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(value = "/create-post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> createPost(@Valid @RequestPart("post") String postJson,
            @RequestPart(value = "media", required = false) MultipartFile[] media,

            @CookieValue("jwt") String jwt) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        CreatePostDTO postDto = mapper.readValue(postJson, CreatePostDTO.class);
        if (postDto.getContent().trim().length() == 0 || postDto.getTitle().trim().length() == 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "The title or content is empty"));

        }
        if (postDto.getContent().trim().length() > 600 || postDto.getTitle().trim().length() > 50) {
            return ResponseEntity.badRequest().body(Map.of("error", "The title or content too large"));

        }
        final long MAX_FILE_SIZE = 4 * 1024 * 1024; // 4 MB

        if (media != null && media.length > 0) {
            for (MultipartFile file : media) {
                if (file != null && !file.isEmpty()) {
                    String contentType = file.getContentType();
                    if (contentType == null ||
                            !(contentType.startsWith("image/") || contentType.startsWith("video/"))) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("error", "Only images and videos are allowed! File rejected: "
                                        + file.getOriginalFilename()));
                    }

                    // Check file size
                    if (file.getSize() > MAX_FILE_SIZE) {
                        return ResponseEntity.badRequest()
                                .body(Map.of("message", "File too large! Max 4MB allowed: "
                                        + file.getOriginalFilename()));
                    }
                }
            }
        }

        postService.createPost(postDto, media != null ? media : new MultipartFile[0], jwt);

        return ResponseEntity.ok(Map.of("message", "Post created successfully"));
    }

}
