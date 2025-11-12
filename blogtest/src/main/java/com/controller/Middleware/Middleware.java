package com.controller.Middleware;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Model.UserStruct;
import com.Repository.UserRepo;
import com.services.JwtService;

@RestController
public class Middleware {
    private final JwtService jwtService;
    private final UserRepo userRepo;
    public Middleware(JwtService jwtService, UserRepo userRepo) {
        this.jwtService = jwtService;
        this.userRepo = userRepo;
    }
    @GetMapping("/middleware")
    public ResponseEntity<Map<String, String>> middleware( @CookieValue("jwt") String jwt) {
        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Invalid JWT token Login please!"));
        }
        UserStruct user = userRepo.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: User not found!"));
        }

        Map<String, String> userDetails = Map.of(
                "username", user.getUsername(),
                "email", user.getMail(),
                "bio", user.getBio(),
                "age", String.valueOf(user.getAge())
        );
      
        return ResponseEntity.ok(Map.of("message", userDetails.toString()));
    }

}
