package com.controller.posts;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

import org.springframework.web.bind.annotation.*;

import com.Model.PostsStruct;
import com.Repository.PostRepo;
import com.services.JwtService;

@RestController
public class UpdatePost {
    private final PostRepo postRepo;
    private final JwtService jwtService;
    public UpdatePost(PostRepo postRepo, JwtService jwtService) {
        this.postRepo = postRepo;
        this.jwtService = jwtService;
    }
    @PutMapping("/update-post")
    public void updatePost(@RequestParam Integer postId, @RequestBody PostsStruct newContent, @CookieValue("jwt") String jwt) {
        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty()) {
            throw new RuntimeException("Error: Invalid JWT token!");
        }
        Optional<PostsStruct> existingPost = postRepo.findById(postId);
        if (!existingPost.isPresent()) {
            throw new RuntimeException("Error: Post not found!");
        }

        if (!existingPost.get().getAuthor().equals(username)) {
            throw new RuntimeException("Error: You are not the author of this post!");
        }
        existingPost.get().setTitle(newContent.getTitle());
        existingPost.get().setContent(newContent.getContent());
        postRepo.save(existingPost.get());
    }
}