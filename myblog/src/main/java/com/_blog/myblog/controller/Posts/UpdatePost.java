package com._blog.myblog.controller.Posts;

import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com._blog.myblog.model.PostStruct;
import com._blog.myblog.repository.PostRepository;
import com._blog.myblog.services.JwtService;

@RestController
public class UpdatePost {
    private final JwtService jwtService;
    private final PostRepository postRepository;

    public UpdatePost(JwtService jwtService, PostRepository postRepository) {
        this.jwtService = jwtService;
        this.postRepository = postRepository;
    }

    @PutMapping("/update-post/{postId}")
    public ResponseEntity<String> UpdatePost(@RequestHeader("Authorization") String token, @PathVariable int postId,
            @RequestBody PostStruct updatedPost) {
        String username = jwtService.extractUsername(token.replace("Bearer ", ""));

        Optional<PostStruct> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Post not found");
        }

        PostStruct post = postOptional.get();

        if (!username.equals(post.getAuthor())) {
            return ResponseEntity.status(403).body("You can only update your own posts");
        }
        post.setText(updatedPost.getText());
        post.setTitle(updatedPost.getTitle());
        postRepository.save(post);
        return ResponseEntity.ok("Post updated successfully");
    }
}
