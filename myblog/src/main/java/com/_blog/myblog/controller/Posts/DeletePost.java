package com._blog.myblog.controller.Posts;

import com._blog.myblog.model.PostStruct;
import com._blog.myblog.repository.PostRepository;
import com._blog.myblog.services.JwtService;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeletePost {

    private final JwtService jwtService;
    private final PostRepository postRepository;

    DeletePost(JwtService jwtService, PostRepository postRepository) {
        this.jwtService = jwtService;
        this.postRepository = postRepository;
    }

@DeleteMapping("/delete-post/{postId}")
public ResponseEntity<String> deletePost(@RequestHeader("Authorization") String token,@PathVariable int postId) {

    String username = jwtService.extractUsername(token.replace("Bearer ", ""));

    Optional<PostStruct> postOptional = postRepository.findById(postId);

    if (postOptional.isEmpty()) {
        return ResponseEntity.status(404).body("Post not found");
    }

    PostStruct post = postOptional.get();

    if (!username.equals(post.getAuthor())) { 
        return ResponseEntity.status(403).body("You can only delete your own posts");
    }

    postRepository.delete(post);
    return ResponseEntity.ok("Post deleted successfully");
}

}
