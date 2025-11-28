package com.controller.Auth;

import java.util.Map;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dto.LoginRequestDTO;
import com.services.AuthService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class LoginAPI {

    private final AuthService authService;

    public LoginAPI(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto, HttpServletResponse response) {
        System.out.println("Login attempt for user: " + dto);
        String token = authService.login(dto);

        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .path("/")
                .maxAge(24 * 60 * 60)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(Map.of("message", "Login successful", "token", token));
    }
}
