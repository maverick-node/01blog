package com._blog.myblog.controller.Middleware;

import java.util.Map;
import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com._blog.myblog.model.UserStruct;
import com._blog.myblog.repository.UserRepository;
import com._blog.myblog.services.JwtService;

@RestController
public class Middleware {
    private JwtService jwtService;
    private UserRepository userRepository;

    public Middleware(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;

    }

    @GetMapping("/middleware")
    public ResponseEntity<Map<String, String>> validateToken(
            @CookieValue(name = "jwt", required = false) String token) {
        try {
            System.out.println("Token received: " + token);
            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "No authentication token provided"));
            }
            String jwtToken = token.startsWith("Bearer ") ? token.replace("Bearer ", "") : token;

            String username = jwtService.extractUsername(jwtToken);

            if (username == null || username.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired token"));
            }

            Optional<UserStruct> user = userRepository.findByusername(username);
            UserStruct dbuser = user.get();

            return ResponseEntity.ok(Map.of("username", username, "token", jwtToken, "mail", dbuser.getMail(), "bio",
                    dbuser.getBio(), "age", dbuser.getAge() + "", "role", dbuser.getRole()));

        } catch (Exception e) {

            return ResponseEntity.status(401).body(Map.of("error", "Token validation failed"));
        }
    }
}
