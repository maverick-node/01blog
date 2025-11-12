package com.controller.Comments;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.Repository.CommentRepo;
import com.services.JwtService;

@RestController
public class DeleteComment {
    private final CommentRepo commentRepo;
    private final JwtService jwtService;

    public DeleteComment(CommentRepo commentRepo, JwtService jwtService) {
        this.commentRepo = commentRepo;
        this.jwtService = jwtService;
    }
    @DeleteMapping("/delete-comment")
    public ResponseEntity<Map<String, String>> deleteComment(@RequestParam Integer commentId,
            @CookieValue("jwt") String jwt) {
        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Invalid JWT token!"));
        }
        if (!commentRepo.existsById(commentId)) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Comment not found!"));
        }
        String commentAuthor = commentRepo.findById(commentId).get().getUsername();
        if (!commentAuthor.equals(username)) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: You are not the author of this comment!"));
        }
        commentRepo.deleteById(commentId);
        return ResponseEntity.ok(Map.of("message", "Comment deleted successfully"));
    }
}