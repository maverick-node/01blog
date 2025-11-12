package com.controller.posts;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Model.PostsStruct;
import com.Model.UserStruct;
import com.Repository.PostRepo;
import com.Repository.UserRepo;
import com.services.JwtService;
import com.services.NotificationService;

@RestController

public class CreatePost {

    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final PostRepo postRepo;
    private final NotificationService notificationService;

    public CreatePost(JwtService jwtService, PostRepo postRepo, UserRepo userRepo,
            NotificationService notificationService) {
        this.jwtService = jwtService;
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.notificationService = notificationService;
    }

    @RequestMapping("/create-post")
    public ResponseEntity<Map<String, String>> createPost(@RequestBody PostsStruct post,
            @CookieValue("jwt") String jwt) {
        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Invalid JWT token!"));
        }
        if (post.getTitle() == null || post.getTitle().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Title is required!"));
        }
        if (post.getContent() == null || post.getContent().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Content is required!"));
        }
        UserStruct user = userRepo.findByUsername(post.getAuthor());
        int userId = user.getId();

        post.setAuthor(username);
        postRepo.save(post);
        // Notification
        int usernameId = userRepo.findByUsername(username).getId();
        if (userId != usernameId) {
            notificationService.createNotification(
                    userId,
                    usernameId,
                    "post",
                    "New post created");
        }
        return ResponseEntity.ok(Map.of("message", "Post created successfully"));

    }
}
