package com._blog.myblog.controller.Auth;

import com._blog.myblog.repository.UserRepository;
import com._blog.myblog.services.JwtService;

import jakarta.servlet.http.HttpServletResponse;

import com._blog.myblog.model.UserStruct;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "http://localhost:4200")
public class LoginAPI {

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public LoginAPI(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> login(@RequestBody UserStruct user, HttpServletResponse response) {
        Optional<UserStruct> optionalUser = userRepository.findByMail(user.getMail());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mail not found"));
        }
System.out.println(user.getPassword());
        UserStruct dbUser = optionalUser.get();
        if (!passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Password Invalid"));
        }

        String token = jwtService.generateToken(dbUser.getUsername());
        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "username", dbUser.getUsername(),
                "token", token)

        );

    }
}
