package com._blog.myblog.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com._blog.myblog.model.UserStruct;
import com._blog.myblog.repository.UserRepository;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserStruct registerUser(String email, String rawPassword, String username, String bio, int age) {
        UserStruct user = new UserStruct();
        user.setMail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setUsername(username); 
        user.setBio(bio);
        user.setAge(age);
        return userRepository.save(user);
    }
}
