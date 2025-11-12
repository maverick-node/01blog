package com.controller.posts;


    
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import com.Repository.PostRepo;
import com.services.JwtService;
import java.util.Map;
@RestController
public class DeletePosts {
    private final JwtService jwtService;
    private final PostRepo postRepo;

    public DeletePosts(JwtService jwtService, PostRepo postRepo) {
        this.jwtService = jwtService;
        this.postRepo = postRepo;
    }

    @DeleteMapping("/delete-post")
    public ResponseEntity<Map<String, String>> deletePost(@RequestParam Integer postId,
            @CookieValue("jwt") String jwt) {
        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Invalid JWT token!"));
        }
        if (!postRepo.existsById(postId)) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Post not found!"));
        }
        String postAuthor = postRepo.findById(postId).get().getAuthor();
        if (!postAuthor.equals(username)) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: You are not the author of this post!"));
        }   
        postRepo.deleteById(postId);
        return ResponseEntity.ok(Map.of("message", "Post deleted successfully"));
    }
}
