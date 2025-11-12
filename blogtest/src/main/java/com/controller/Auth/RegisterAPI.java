package com.controller.Auth;

import org.springframework.web.bind.annotation.RestController;

import com.Repository.*;
import com.Model.UserStruct;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class RegisterAPI {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public RegisterAPI(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> Register(@RequestBody UserStruct user) {
        if (user.getMail() == null || user.getMail().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Email is required!"));
        }

        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Username is required!"));
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Password is required!"));
        }

        if (user.getAge() == 0 || user.getAge() < 13) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Age must be at least 13!"));
        }
        if (user.getBio() == null || user.getBio().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Bio is required!"));
        }

        if (userRepo.existsByMail(user.getMail())) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Email is already in use!"));
        }
        if (userRepo.existsByUsername(user.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Username is already taken!"));
        }
        if (user.getPassword().length() < 6 || user.getPassword().length() > 20) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Password must be at least 6 characters long!"));
        }
        String HashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(HashedPassword);
        userRepo.save(user);
        return ResponseEntity
                .ok()
                .body(Map.of("message", "User registered successfully!"));
    }
}
