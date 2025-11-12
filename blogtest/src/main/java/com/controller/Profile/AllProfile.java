package com.controller.Profile;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import com.Repository.UserRepo;
import com.services.JwtService;

public class AllProfile {
    private final JwtService jwtService;
    private final UserRepo userRepo;

    public AllProfile(JwtService jwtService, UserRepo userRepo) {
        this.jwtService = jwtService;
        this.userRepo = userRepo;
    }

    @GetMapping("/get-users")
    public ResponseEntity<Map<String, String>> getAllUsers(@CookieValue("jwt") String jwt) {
        String username = jwtService.extractUsername(jwt);
        if (userRepo.existsByUsername(username)) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Invalid JWT token!"));
        }
        return ResponseEntity.ok(Map.of("users", userRepo.findAll().toString()));
    }
}
