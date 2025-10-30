package com._blog.myblog.controller.Comments;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com._blog.myblog.model.CommentStruct;
import com._blog.myblog.model.PostStruct;
import com._blog.myblog.repository.CommentRepository;
import com._blog.myblog.repository.PostRepository;
import com._blog.myblog.services.JwtService;

@RestController
public class DeleteComment {

    private final CommentRepository commentRepository;

    private final JwtService jwtService;

    public DeleteComment(JwtService jwtService, CommentRepository commentRepository) {
        this.jwtService = jwtService;
        this.commentRepository = commentRepository;
    }

    @DeleteMapping("/delete-comment/{commentid}")
    public ResponseEntity<String> DeleteComment(@RequestHeader("Authorization") String token, @PathVariable int commentid) {
        String username = jwtService.extractUsername(token.replace("Bearer ", ""));
        System.out.println(commentid);
        Optional<CommentStruct> com = commentRepository.findById(commentid);
         if (com.isEmpty()) {
        return ResponseEntity.status(404).body("comment not found");
    }

    CommentStruct comment = com.get();

    if (!username.equals(comment.getUsername())) { 
        return ResponseEntity.status(403).body("You can only delete your own comments");
    }

    commentRepository.delete(comment);
    return ResponseEntity.ok("comment deleted successfully");

    }
}
