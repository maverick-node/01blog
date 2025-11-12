package com._blog.myblog.controller.Likes;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com._blog.myblog.services.*;
import com._blog.myblog.model.LikesStruct;
import com._blog.myblog.model.PostStruct;
import com._blog.myblog.model.UserStruct;
import com._blog.myblog.repository.LikesRepository;
import com._blog.myblog.repository.PostRepository;
import com._blog.myblog.repository.UserRepository;
import com._blog.myblog.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/likes")
public class LikesController {
    private final LikesRepository likesRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PostRepository postRepository;

    @Autowired
    private NotificationService notificationService;

    public LikesController(LikesRepository likesRepository,
            UserRepository userRepository,
            PostRepository postRepository,
            JwtService jwtService) {
        this.likesRepository = likesRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.jwtService = jwtService;
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Map<String, String>> addLike(@PathVariable int postId,
            @RequestHeader("Authorization") String token) {

    
        String username = jwtService.extractUsername(token.replace("Bearer ", ""));
        UserStruct user = userRepository.findByusername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LikesStruct existingLike = likesRepository.findByPostIdAndUserId(postId, user.getId());

        if (existingLike != null) {
            existingLike.setLiked(!existingLike.getLiked());
            likesRepository.save(existingLike);

            String message = existingLike.getLiked() ? "Post liked successfully" : "Post unliked successfully";

            if (existingLike.getLiked()) {
                notifyPostOwner(postId, user);
            }

            return ResponseEntity.ok(Map.of("message", message));
        }

        LikesStruct newLike = new LikesStruct();
        newLike.setPostId(postId);
        newLike.setUserId(user.getId());
        newLike.setLiked(true);
        likesRepository.save(newLike);

        notifyPostOwner(postId, user);

        return ResponseEntity.ok(Map.of("message", "Post liked successfully"));
    }

    private void notifyPostOwner(int postId, UserStruct liker) {
        Optional<PostStruct> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty())
            return;

        PostStruct post = optionalPost.get();
        String usrp = post.getAuthor();
        Optional<UserStruct> uuss = userRepository.findByusername(usrp);
        UserStruct lastuser = uuss.get();
        int postOwnerId = lastuser.getId();

        if (postOwnerId == liker.getId())
            return;

        notificationService.createNotification(
                postOwnerId,
                liker.getId(),
                "NEW_LIKE",
                liker.getUsername() + " liked your post.");
    }
    @GetMapping("/count/{postId}")
    public ResponseEntity<Map<String, Long>> getLikeCount(@PathVariable int postId) {
        long likeCount = likesRepository.countByPostIdAndLikedTrue(postId);
    
        
        return ResponseEntity.ok(Map.of("likeCount", likeCount));
    }
}