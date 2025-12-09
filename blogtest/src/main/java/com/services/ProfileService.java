package com.services;

import org.springframework.stereotype.Service;

import com.Exceptions.InvalidJwtTokenException;
import com.Exceptions.UserNotFoundException;
import com.Repository.UserRepo;
import com.dto.UserProfileDTO;

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
                user.getAge()
                , user.isBanned()
        );
    }
}
