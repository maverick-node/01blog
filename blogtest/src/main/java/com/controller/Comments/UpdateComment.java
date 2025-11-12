package com.controller.Comments;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Model.CommentStruct;
import com.Repository.CommentRepo;
import com.services.JwtService;

@RestController
public class UpdateComment {
    private final CommentRepo commentRepo;
    private final JwtService jwtService;

    public UpdateComment(CommentRepo commentRepo, JwtService jwtService) {
        this.commentRepo = commentRepo;
        this.jwtService = jwtService;
    }

    @PutMapping("/update-comment")
    public ResponseEntity<Map<String, String>> updateComment(@RequestParam Integer commentId,
            @RequestParam String newContent,
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
        Optional<CommentStruct> commentOpt = commentRepo.findById(commentId);
        if (commentOpt.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Comment not found!"));
        }
        CommentStruct comment = commentOpt.get();
        if (!comment.getUsername().equals(username)) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: You are not the author of this comment!"));
        }
        comment.setComment(newContent);
        commentRepo.save(comment);
        return ResponseEntity.ok(Map.of("message", "Comment updated successfully"));

    }
}
