package com.services;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Exceptions.UserAlreadyExistsException;
import com.Model.UserStruct;
import com.Repository.UserRepo;
import com.dto.RegisterRequestDTO;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(RegisterRequestDTO dto) {

        Integer age1 = Integer.valueOf(dto.getAge());

        if (userRepo.existsByMail(dto.getMail().toLowerCase())) {
            throw new UserAlreadyExistsException("Email already in use!");
        }

        if (userRepo.existsByUsername(dto.getUsername().toLowerCase())) {
            throw new UserAlreadyExistsException("Username already taken!");
        }
        // email interdit special char and max length 50
        if (dto.getMail().length() > 50 || !dto.getMail().matches("^[a-zA-Z0-9@.]+$")) {
            throw new IllegalArgumentException("Invalid email format!");
        }
        UserStruct user = new UserStruct();
        user.setMail(dto.getMail().toLowerCase());
        user.setUsername(dto.getUsername().toLowerCase());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setAge(age1);
        user.setBio(dto.getBio());
        user.setUserUuid(UUID.randomUUID().toString());


        userRepo.save(user);
    }
}
