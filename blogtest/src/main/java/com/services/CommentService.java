package com.services;

import org.springframework.stereotype.Service;

import com.Exceptions.CommentNotFoundException;
import com.Exceptions.InvalidJwtTokenException;
import com.Exceptions.UnauthorizedActionException;
import com.Model.CommentStruct;
import com.Repository.CommentRepo;
import com.Repository.PostRepo;
import com.dto.CreateCommentDTO;
import com.dto.UpdateCommentDTO;

@Service
public class CommentService {
    private final PostRepo postRepo;
    private final CommentRepo commentRepo;
    private final JwtService jwtService;

    public CommentService(PostRepo postRepo, CommentRepo commentRepo, JwtService jwtService) {
        this.postRepo = postRepo;
        this.commentRepo = commentRepo;
        this.jwtService = jwtService;
    }

    public void deleteComment(Integer commentId, String jwt) {

        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty()) {
            throw new RuntimeException("Invalid JWT token");
        }

        var comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUsername().equals(username)) {
            throw new RuntimeException("You are not the author of this comment");
        }

        commentRepo.delete(comment);
    }

    public void updateComment(UpdateCommentDTO dto, String jwt) {
        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty()) {
            throw new InvalidJwtTokenException("Invalid JWT token");
        }

        CommentStruct comment = commentRepo.findById(dto.getCommentId())
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));

        if (!comment.getUsername().equals(username)) {
            throw new UnauthorizedActionException("You are not the author of this comment");
        }

        comment.setComment(dto.getNewContent());
        commentRepo.save(comment);
    }
    public CommentStruct createComment(CreateCommentDTO dto, String jwt) {
        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty()) {
            throw new InvalidJwtTokenException("Invalid JWT token");
        }

        // Optional: verify that the post exists
        if (!postRepo.existsById(dto.getPostId())) {
            throw new RuntimeException("Post not found");
        }

        CommentStruct comment = new CommentStruct();
        comment.setPostId(dto.getPostId());
        comment.setUsername(username);
        comment.setComment(dto.getContent());

        return commentRepo.save(comment);
    }
}
