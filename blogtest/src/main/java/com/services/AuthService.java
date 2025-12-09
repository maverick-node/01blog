package com.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Exceptions.InvalidCredentialsException;
import com.Exceptions.UserNotFoundException;
import com.Model.UserStruct;
import com.Repository.UserRepo;
import com.dto.LoginRequestDTO;

@Service
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    

    public AuthService(UserRepo userRepo, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String login(LoginRequestDTO dto) {

        UserStruct user = userRepo.findByMail(dto.getEmail());
        if (user == null) {
            throw new UserNotFoundException("Username not found");
        }
        if (user.isBanned()) {
            throw new InvalidCredentialsException("You are  banned");
            
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Incorrect password");
        }

        return jwtService.generateToken(user.getUsername());
    }
}
