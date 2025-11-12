package com._blog.myblog.controller.Profile;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com._blog.myblog.model.PostStruct;
import com._blog.myblog.model.UserStruct;
import com._blog.myblog.repository.PostRepository;
import com._blog.myblog.repository.UserRepository;
import com._blog.myblog.services.JwtService;

@CrossOrigin
@RestController
public class PublicProfile {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final JwtService jwtService;

    public PublicProfile(UserRepository userRepository, PostRepository postRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.jwtService = jwtService;
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<Map<String, Object>> getPublicProfile(@PathVariable String username, @CookieValue(name = "jwt", required = false) String token) {
        String logged = jwtService.extractUsername(token.replace("Bearer ", ""));
        if (userRepository.existsByUsername(logged)) {
            return ResponseEntity.status(403).body(Map.of("message", "Access denied login please"));
        }
        var optional = userRepository.findByusername(username);
        if (optional.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }

        UserStruct user = optional.get();
        List<PostStruct> posts = postRepository.findByAuthor(username);

        return ResponseEntity.ok(Map.of(
            "id", user.getId(),
            "username", user.getUsername(),
            "bio", user.getBio(),
            "age", user.getAge(),
            "posts", posts
        ));
    }

 
}
