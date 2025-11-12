package com.controller.Auth;

import com.Repository.*;
import com.services.JwtService;

import jakarta.servlet.http.HttpServletResponse;

import com.Model.UserStruct;

import java.util.Map;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginAPI {
    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public LoginAPI(UserRepo userRepo, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")

    public ResponseEntity<Map<String, String>> Login(@RequestBody UserStruct user,HttpServletResponse response ) {
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

        if (!userRepo.existsByUsername(user.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Username not found!"));
        }
        UserStruct foundUser = userRepo.findByUsername(user.getUsername());

       if (!passwordEncoder.matches(user.getPassword(), foundUser.getPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Incorrect password!"));
        }

        String token = jwtService.generateToken(foundUser.getUsername());
         ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity
                .ok()
                .body(Map.of("message", "Login successful!"));
    }

}