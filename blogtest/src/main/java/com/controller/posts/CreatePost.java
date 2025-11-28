package com.controller.posts;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Model.NotificationStruct;
import com.Model.PostsStruct;
import com.Model.UserStruct;
import com.Repository.NotificationRepo;
import com.Repository.PostRepo;
import com.Repository.UserRepo;
import com.dto.CreatePostDTO;
import com.services.JwtService;
import com.services.PostService;

@RestController

public class CreatePost {


    private final PostService postService;

    public CreatePost(PostService postService) {
        this.postService = postService;
    }

    @RequestMapping("/create-post")
    public ResponseEntity<Map<String, String>> createPost(@RequestBody CreatePostDTO post,
            @CookieValue("jwt") String jwt) {
        System.out.println("Create post request received: " + post);
        postService.createPost(post, jwt);
        return ResponseEntity.ok(Map.of("message", "Post created successfully"));

    }
}
