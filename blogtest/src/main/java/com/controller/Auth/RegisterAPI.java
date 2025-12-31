package com.controller.Auth;

import com.dto.RegisterRequestDTO;
import com.services.JwtService;
import com.services.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
public class RegisterAPI {

    private final UserService userService;
    private final JwtService jwtService;

    public RegisterAPI(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequestDTO user,
            BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> {
                errors.put("error", error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errors);
        }


        userService.registerUser(user);

        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }

}
