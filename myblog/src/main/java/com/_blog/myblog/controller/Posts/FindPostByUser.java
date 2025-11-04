package com._blog.myblog.controller.Posts;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com._blog.myblog.model.PostStruct;
import com._blog.myblog.repository.PostRepository;
import com._blog.myblog.services.JwtService;

@RestController
public class FindPostByUser {
    private final JwtService jwtService;
    private final PostRepository postRepository;
    
    public FindPostByUser(JwtService jwtService , PostRepository postRepository) {
        this.jwtService = jwtService;
        this.postRepository = postRepository;
    }
    @GetMapping("/posts-by-user")
    public ResponseEntity<List<PostStruct>> findPostsByUser(@CookieValue(name = "jwt", required = false) String token) {
        System.out.println("Token received: " + token);

        if (token == null) {
            return ResponseEntity.badRequest().build();
        }
        
        String username = jwtService.extractUsername(token.replace("Bearer ", ""));
        System.out.println("Username extracted: " + username);
       
        System.out.println("Username extracted: " + username);
        List<PostStruct> posts = postRepository.findByAuthor(username);
        System.out.println("Posts found: " + posts);
        
        return ResponseEntity.ok(posts);

    }
}
