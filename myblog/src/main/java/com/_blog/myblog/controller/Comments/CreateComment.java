package com._blog.myblog.controller.Comments;

import com._blog.myblog.repository.PostRepository;
import com._blog.myblog.repository.UserRepository;
import com._blog.myblog.services.JwtService;
import com._blog.myblog.services.NotificationService;
import com._blog.myblog.services.UserService;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com._blog.myblog.model.CommentStruct;
import com._blog.myblog.model.PostStruct;
import com._blog.myblog.model.UserStruct;

@RestController
public class CreateComment {

    private final NotificationService notificationService;

    private final UserService userService;

    private final PostRepository postRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    CreateComment(JwtService jwtService, UserService userService,
            PostRepository postRepository, NotificationService notificationService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.postRepository = postRepository;
        this.notificationService = notificationService;
        this.userRepository = userRepository;

    }

    @PostMapping("/create-comment")
    public ResponseEntity<Map<String, String>> createComment(
            @RequestBody CommentStruct comment,
            @CookieValue(name = "jwt", required = false) String token) {
            
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication token is required"));
        }

        int postId = comment.getPostId();
        if (!postRepository.existsById(postId)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Post not found"));
        }

        String username;
        try {
            username = jwtService.extractUsername(token.replace("Bearer ", ""));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid authentication token"));
        }
        Optional<UserStruct> optionalUser = userRepository.findByusername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "User not found"));
        }
        UserStruct dbUser = optionalUser.get();
        userService.saveComment(username, comment.getComment(), postId);

        Optional<PostStruct> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Post not found"));
        }
        PostStruct post = optionalPost.get();
        String userF = post.getAuthor();
        Optional<UserStruct> ss = userRepository.findByusername(userF);
        UserStruct last = ss.get();
        if (!username.equals(last.getUsername())) {

            if (post.getId() != dbUser.getId()) {
                notificationService.createNotification(
                        last.getId(),
                        dbUser.getId(),
                        "NEW_COMMENT",
                        dbUser.getUsername() + " commented on your post.");
            }
        }

        notificationService.createNotification(
                dbUser.getId(),
                dbUser.getId(),
                "COMMENT_POST",
                "You commented on " + post.getTitle() + " post.");

        return ResponseEntity.ok(Map.of("message", "Comment created successfully!"));
    }

}
