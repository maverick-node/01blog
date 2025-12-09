package com.controller.Likes;

import java.util.List;
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
import com.services.LikesService;

@RestController
public class Likes {
    private final LikesRepo likesRepo;
    private final PostRepo postRepo;
    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final LikesService likesService;

    public Likes(LikesRepo likesRepo, PostRepo postRepo, UserRepo userRepo, JwtService jwtService,
            LikesService likesService) {
        this.likesRepo = likesRepo;
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.likesService = likesService;
    }

    @PostMapping("/like-post/{postId}")
    public ResponseEntity<Map<String, String>> likePost(
            @CookieValue("jwt") String jwt,
            @PathVariable("postId") int postId) {
        System.out.println("Like post request for post ID: " + postId);
        Boolean liked = likesService.toggleLike(postId, jwt);
        if (liked == true) {
            return ResponseEntity.ok(Map.of("message", "Liked"));
        }
        return ResponseEntity.ok(Map.of("message", "Unliked"));
    }

    @GetMapping("/likes/count/{post-id}")
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
        List<LikesStruct> likes = likesRepo.findByPostId(PostId);
        long likeCount = likes.stream().filter(LikesStruct::getLiked).count();
        return ResponseEntity.ok(Map.of("likeCount", likeCount));
    }

}
