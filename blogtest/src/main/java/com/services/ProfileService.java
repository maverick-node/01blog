package com.services;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.Exceptions.InvalidJwtTokenException;
import com.Exceptions.UserNotFoundException;
import com.Model.UserStruct;
import com.Repository.UserRepo;
import com.dto.UserProfileDTO;

import jakarta.validation.Valid;

@Service
public class ProfileService {

    private final JwtService jwtService;
    private final UserRepo userRepo;
   

    public ProfileService(JwtService jwtService, UserRepo userRepo) {
        this.jwtService = jwtService;
        this.userRepo = userRepo;
  
    }

    public UserProfileDTO getProfile(String username, String jwt) {
        String tokenUsername = jwtService.extractUsername(jwt);

        if (tokenUsername == null || !userRepo.existsByUsername(tokenUsername)) {
            throw new InvalidJwtTokenException("Invalid JWT token");
        }

        var user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return new UserProfileDTO(
                user.getUsername(),
                user.getMail(),
                user.getBio(),
                user.getAge(), user.isBanned());
    }

    public ResponseEntity<Map<String, String>> editmyinfo(@Valid UserProfileDTO info, String jwt) {


        String username = jwtService.extractUsername(jwt);

    
        UserStruct user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }


        if (info.getAge() != 0 ) {
            user.setAge(info.getAge()); 
        }
        if (info.getBio() != null && !info.getBio().isBlank()) {
            user.setBio(info.getBio());
        }
        if (info.getEmail() != null && !info.getEmail().isBlank()) {
            user.setMail(info.getEmail());
        }
        if (info.getUsername() != null && !info.getUsername().equals(username)) {
            throw new IllegalArgumentException("You can't change your username");
        }

        // Save changes
        userRepo.save(user);

        return ResponseEntity.ok(Map.of("message", "updated"));
    }

}
