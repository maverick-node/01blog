package com._blog.myblog.controller.Comments;

import com._blog.myblog.repository.CommentRepository;
import com._blog.myblog.repository.PostRepository;
import com._blog.myblog.services.JwtService;
import com._blog.myblog.services.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com._blog.myblog.model.CommentStruct;

@RestController
public class CreateComment {

    private final UserService userService;

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final JwtService jwtService;

    CreateComment(JwtService jwtService, CommentRepository commentRepository, UserService userService,
            PostRepository postRepository) {
        this.jwtService = jwtService;
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.postRepository = postRepository;
    }

    @PostMapping("/create-comment")
    public ResponseEntity<String> createComment(
            @RequestBody CommentStruct comment,
            @RequestHeader("Authorization") String token) {
        int postId = comment.getPostId();
        boolean exist = postRepository.existsById(postId);
        if (!exist) {
            return ResponseEntity.badRequest().body("post not found");
        }
        String username = jwtService.extractUsername(token.replace("Bearer ", ""));
        userService.saveComment(username, comment.getComment(), postId);
        return ResponseEntity.ok("comment created");
    }

}
