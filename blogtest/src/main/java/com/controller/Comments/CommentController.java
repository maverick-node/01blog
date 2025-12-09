package com.controller.Comments;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.CreateCommentDTO;
import com.services.CommentService;

@RestController
@RequestMapping
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {

        this.commentService = commentService;
    }

    @PostMapping("/create-comment")
    public ResponseEntity<?> create(
            @RequestBody CreateCommentDTO dto,
            @CookieValue("jwt") String jwt) {
        System.out.println("Create comment request received: " + dto);
        commentService.createComment(dto, jwt);

        return ResponseEntity.ok(Map.of("message", "Comment created successfully"));
    }
}
