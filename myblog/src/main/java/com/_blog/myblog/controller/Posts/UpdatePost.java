package com._blog.myblog.controller.Posts;

import java.util.Map;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<Map<String, String>> UpdatePost(@CookieValue(name = "jwt", required = false) String token, 
            @PathVariable String postId, @RequestBody Map<String, String> updateData) {
        
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Authentication required"));
        }

        try {
            String username = jwtService.extractUsername(token);
            Integer postIdInt = Integer.parseInt(postId);

            Optional<PostStruct> postOptional = postRepository.findById(postIdInt);
            if (postOptional.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Post not found"));
            }

            PostStruct post = postOptional.get();

            if (!username.equals(post.getAuthor())) {
                return ResponseEntity.status(403).body(Map.of("error", "You can only update your own posts"));
            }

            String newTitle = updateData.get("title");
            String newText = updateData.get("text");
            
            if (newTitle != null && !newTitle.trim().isEmpty()) {
                post.setTitle(newTitle.trim());
            }
            
            if (newText != null && !newText.trim().isEmpty()) {
                post.setText(newText.trim());
            }

            postRepository.save(post);
            return ResponseEntity.ok(Map.of("message", "Post updated successfully"));
            
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body(Map.of("error", "Invalid post ID format"));
        } catch (Exception e) {
            System.err.println("Error updating post: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }
}