package com.controller.Comments;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.services.CommentService;

@RestController
public class DeleteComment {

    private final CommentService commentService;

    public DeleteComment(CommentService commentService) {

        this.commentService = commentService;
    }

    @DeleteMapping("delete-comment/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id, @CookieValue("jwt") String jwt) {
        commentService.deleteComment(id, jwt);
        return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
    }

}