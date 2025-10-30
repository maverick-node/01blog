package com._blog.myblog.controller.Comments;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com._blog.myblog.model.CommentStruct;
import com._blog.myblog.repository.CommentRepository;
import com._blog.myblog.services.JwtService;

@RestController
public class UpdatedComment {
    private JwtService jwtService;
    private CommentRepository commentRepository;

    public UpdatedComment(JwtService jwtService, CommentRepository commentRepository) {
        this.jwtService = jwtService;
        this.commentRepository = commentRepository;

    }

    @PutMapping("/update-comment/{commentid}")
    public ResponseEntity<String> UpdatePost(@RequestHeader("Authorization") String token, @PathVariable int commentid,
            @RequestBody CommentStruct updatedComment) {

        String username = jwtService.extractUsername(token.replace("Bearer ", ""));

        Optional<CommentStruct> commentOptional = commentRepository.findById(commentid);
        if (commentOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Comment not found");
        }

        CommentStruct com = commentOptional.get();

        if (!username.equals(com.getUsername())) {
            return ResponseEntity.status(403).body("You can only update your own comment");
        }
        com.setComment(updatedComment.getComment());

        commentRepository.save(com);
        return ResponseEntity.ok("Comment updated successfully");

    }
}