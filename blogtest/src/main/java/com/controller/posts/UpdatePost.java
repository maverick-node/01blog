package com.controller.posts;

import java.util.Map;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Model.PostsStruct;

import com.services.PostService;

@RestController
public class UpdatePost {

    private final PostService postService ;
    public UpdatePost(PostService postService) {
        this.postService = postService;
    }
    @PutMapping("/update-post/{postId}")
    public ResponseEntity<Map<String, String>> updatePost(
            @PathVariable Integer postId,
            @RequestBody PostsStruct newContent,
            @CookieValue("jwt") String jwt) {
                System.out.println("Update post request received for postId: " + postId + " with new content: " + newContent);
        postService.updatePost(postId, newContent, jwt);

        return ResponseEntity.ok(Map.of("message", "Post updated successfully"));
    }
}