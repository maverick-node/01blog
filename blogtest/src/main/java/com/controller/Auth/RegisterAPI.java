package com.controller.Auth;


import com.dto.RegisterRequestDTO;
import com.services.UserService;


import java.util.Map;


import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
public class RegisterAPI {

    private final UserService userService;

    public RegisterAPI(UserService userService) {
        this.userService = userService;
  
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> Register(@RequestBody RegisterRequestDTO user) {
            userService.registerUser(user);
            
        return ResponseEntity
                .ok()
                .body(Map.of("message", "User registered successfully!"));
    }
}
