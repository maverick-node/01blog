package com.controller.posts;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.services.PostService;

import java.util.Map;

@RestController
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @DeleteMapping("/delete-post/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(
            @PathVariable Integer postId,
            @CookieValue("jwt") String jwt) {

        postService.deletePost(postId, jwt);

        return ResponseEntity.ok(Map.of("message", "Post deleted successfully"));
    }
}
