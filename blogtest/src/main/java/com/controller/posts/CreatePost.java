package com.controller.posts;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Model.NotificationStruct;
import com.Model.PostsStruct;
import com.Model.UserStruct;
import com.Repository.NotificationRepo;
import com.Repository.PostRepo;
import com.Repository.UserRepo;
import com.dto.CreatePostDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.services.JwtService;
import com.services.PostService;

@RestController

public class CreatePost {

    private final PostService postService;

    public CreatePost(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(value = "/create-post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> createPost(
            @RequestPart("post") String postJson,
            @RequestPart(value = "media", required = false) MultipartFile[] media,
            @CookieValue("jwt") String jwt) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        CreatePostDTO postDto = mapper.readValue(postJson, CreatePostDTO.class);

        // Validate media: only images allowed
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
                }
            }
        }

        postService.createPost(postDto, media != null ? media : new MultipartFile[0], jwt);

        return ResponseEntity.ok(Map.of("message", "Post created successfully"));
    }
}
