package com.controller.Profile;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.Repository.UserRepo;
import com.services.JwtService;

public class ProfileUsers {
    private final JwtService jwtService;
    private final UserRepo userRepo;

    public ProfileUsers(JwtService jwtService, UserRepo userRepo) {
        this.jwtService = jwtService;
        this.userRepo = userRepo;
    }
    @GetMapping("/profile-user/{username}")
    public ResponseEntity<Map<String, String>> getProfileUser(@PathVariable String username, @CookieValue("jwt") String jwt) {
        String tokenUsername = jwtService.extractUsername(jwt);
        if (userRepo.existsByUsername(tokenUsername)) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Invalid JWT token Login please!"));

        }
        var user = userRepo.findByUsername(username);
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
        return ResponseEntity.ok(userDetails);
    }

}
