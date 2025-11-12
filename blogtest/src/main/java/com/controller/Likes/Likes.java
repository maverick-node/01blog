package com.controller.Likes;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Model.LikesStruct;
import com.Repository.LikesRepo;
import com.Repository.PostRepo;
import com.Repository.UserRepo;
import com.services.JwtService;

@RestController
public class Likes {
    private final LikesRepo likesRepo;
    private final PostRepo postRepo;
    private final UserRepo userRepo;
    private final JwtService jwtService;

    public Likes(LikesRepo likesRepo, PostRepo postRepo, UserRepo userRepo, JwtService jwtService) {
        this.likesRepo = likesRepo;
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.jwtService = jwtService;
    }

    @PostMapping("/like-post/{postId}")
    public ResponseEntity<Map<String, String>> likePost(@CookieValue("jwt") String jwt,
            @PathVariable("postId") String postId) {
        // Implement your like post logic here
        int PostId = Integer.parseInt(postId);
        String username = jwtService.extractUsername(jwt);
        if (!userRepo.existsByUsername(username)) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }
        int userID = userRepo.findByUsername(username).getId();
        if (!postRepo.existsById(PostId)) {
            return ResponseEntity.status(404).body(Map.of("message", "Post not found"));
        }
        LikesStruct likesSave = new LikesStruct();

        LikesStruct existingLike = likesRepo.findByPostIdAndUserId(PostId, userID);

        if (existingLike.getLiked()) {
            likesSave.setLiked(false);
        } else {
            likesSave.setLiked(true);
        }
        likesSave.setPostId(PostId);
        likesSave.setUserId(userID);
        likesRepo.save(likesSave);

        return ResponseEntity.ok(Map.of("message", "Post liked successfully"));

    }

    @GetMapping("/get-like/{post-id}")
    public ResponseEntity<Map<String, Object>> getLikes(@PathVariable("post-id") String postId,
            @CookieValue("jwt") String jwt) {
        String username = jwtService.extractUsername(jwt);
        if (!userRepo.existsByUsername(username)) {
            return ResponseEntity.badRequest().body(Map.of("message", "You need to login"));
        }

        int PostId = Integer.parseInt(postId);
        if (!postRepo.existsById(PostId)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Post not created"));
        }
        int likeCount = likesRepo.countByPostId(PostId);
        return ResponseEntity.ok(Map.of("likeCount", likeCount));
    }

}
