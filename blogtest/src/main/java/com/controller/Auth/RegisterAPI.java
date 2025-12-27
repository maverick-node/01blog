package com.controller.Auth;

import com.dto.RegisterRequestDTO;
import com.services.UserService;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
public class RegisterAPI {

    private final UserService userService;

    public RegisterAPI(UserService userService) {
        this.userService = userService;

    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequestDTO user,
            BindingResult bindingResult) {
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
