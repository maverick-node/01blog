package com.controller.Comments;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.Model.CommentStruct;
import com.Repository.CommentRepo;
import com.Repository.PostRepo;
import com.services.JwtService;
import com.services.NotificationService;

@RestController
public class CreateComment {
    private final CommentRepo commentRepo;
    private final PostRepo postRepo;
    private final JwtService jwtService;
    private final NotificationService notificationService;

    public CreateComment(CommentRepo commentRepo, PostRepo postRepo, JwtService jwtService,
            NotificationService notificationService) {
        this.commentRepo = commentRepo;
        this.postRepo = postRepo;
        this.jwtService = jwtService;
        this.notificationService = notificationService;
    }

    public ResponseEntity<Map<String, String>> createComment(@RequestBody CommentStruct comment,
            @CookieValue("jwt") String jwt) {
        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Invalid JWT token!"));
        }
        if (comment.getPostId() == 0 || !postRepo.existsById(comment.getPostId())) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Post not found!"));
        }
        if (comment.getComment() == null || comment.getComment().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Content is required!"));
        }
        comment.setUsername(username);
        commentRepo.save(comment);
        String postAuthor = postRepo.findById(comment.getPostId()).get().getAuthor();
        if (!postAuthor.equals(username)) {
            // Create a notification for the comment
            int postAuthorId = postRepo.findById(comment.getPostId()).get().getId();
            int usernameId = commentRepo.findById(comment.getId()).get().getId();
            notificationService.createNotification(
                    postAuthorId,
                    usernameId,
                    "comment",
                    "New comment on your post");
        }
        return ResponseEntity.ok(Map.of("message", "Comment created successfully"));
    }
}
