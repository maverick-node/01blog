package com.services;

import org.springframework.stereotype.Service;

import com.Exceptions.InvalidJwtTokenException;
import com.Exceptions.UserNotFoundException;
import com.Model.UserStruct;
import com.Repository.UserRepo;

@Service
public class UserServiceMiddle {

    private final JwtService jwtService;
    private final UserRepo userRepo;

    public UserServiceMiddle(JwtService jwtService, UserRepo userRepo) {
        this.jwtService = jwtService;
        this.userRepo = userRepo;
    }

    public UserStruct getUserFromJwt(String jwt) {
        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty()) {
            throw new UserNotFoundException("Invalid JWT token");
        }

        UserStruct user = userRepo.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return user;
    }
}
