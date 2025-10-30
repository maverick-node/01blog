package com._blog.myblog.controller.Comments;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com._blog.myblog.model.CommentStruct;
import com._blog.myblog.repository.CommentRepository;

@RestController
public class GetComment {

    private final CommentRepository commentRepository;

    public GetComment(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @GetMapping("/posts/{postId}/comments")
    public List<CommentStruct> getComments(@PathVariable int postId) {
        System.out.println("Post ID = " + postId);
        return commentRepository.findByPostId(postId);
    }

}
