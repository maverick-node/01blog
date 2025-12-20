package com.controller.Profile;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Model.UserStruct;
import com.Repository.UserRepo;
import com.dto.UserDTOMiddle;
import com.services.JwtService;

@RestController
public class AllProfile {
    private final JwtService jwtService;
    private final UserRepo userRepo;

    public AllProfile(JwtService jwtService, UserRepo userRepo) {
        this.jwtService = jwtService;
        this.userRepo = userRepo;
    }

    @GetMapping("/get-users")
    public ResponseEntity<Map<String, Object>> getAllUsers(@CookieValue("jwt") String jwt) {
        String username = jwtService.extractUsername(jwt);
        System.out.println("Username from JWT: " + username);
        if (!userRepo.existsByUsername(username)) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Invalid JWT token!"));
        }
        
        List<UserStruct> users = userRepo.findAll();
        List<UserDTOMiddle> allUsers = new ArrayList<>();
        for (UserStruct u : users) {
            if (u.getUsername().equals(username)){
                continue;
            }
        allUsers.add(new UserDTOMiddle(
                u.getUsername(),
                u.getMail(),
                u.getBio(),
                u.getAge(),
                u.getRole(),
                u.isBanned()

        ));
    }


        return ResponseEntity.ok(Map.of("users", allUsers));
    }
}
