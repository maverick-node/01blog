package com.services;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.Exceptions.BadRequestException;
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

        if (user.isBanned()) {
            throw new BadRequestException("You are banned");

        }

        if (info.getEmail() != null && !info.getEmail().isBlank()) {
            if (userRepo.existsByMail(info.getEmail().toLowerCase()) && !info.getEmail().equals(user.getMail())) {

                throw new BadRequestException("Email is already in use");
            }
        }


       //no speacial characters allowed in mail allow @ and . and max length 50
       if (info.getEmail() != null && (info.getEmail().length() > 50 || !info.getEmail().matches("^[a-zA-Z0-9@.]+$"))) {
            throw new BadRequestException("Invalid email format");
        }

        if (info.getBio() != null && info.getBio().trim().length() > 300) {
            throw new BadRequestException("Bio is too long");
        }
        if (info.getAge() < 0 || info.getAge() > 150) {
            throw new BadRequestException("Invalid age");
        }

        if (info.getAge() != 0) {
            user.setAge(info.getAge());
        }
        if (info.getBio() != null && !info.getBio().isBlank()) {
            user.setBio(info.getBio().trim());
        }
        if (info.getEmail() != null && !info.getEmail().isBlank()) {
            user.setMail(info.getEmail().toLowerCase());
        }
        if (info.getUsername() != null && !info.getUsername().equals(username)) {
            throw new BadRequestException("You can't change your username");
        }

        // Save changes
        userRepo.save(user);

        return ResponseEntity.ok(Map.of("message", "updated"));
    }

}
