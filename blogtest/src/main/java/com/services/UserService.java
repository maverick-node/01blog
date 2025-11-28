package com.services;

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

        if (userRepo.existsByMail(dto.getMail())) {
            throw new UserAlreadyExistsException("Email already in use!");
        }

        if (userRepo.existsByUsername(dto.getUsername())) {
            throw new UserAlreadyExistsException("Username already taken!");
        }

        UserStruct user = new UserStruct();
        user.setMail(dto.getMail());
       user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setAge(age1);
        user.setBio(dto.getBio());

        userRepo.save(user);
    }
}
