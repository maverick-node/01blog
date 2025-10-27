package com._blog.myblog.controller;

import com._blog.myblog.repository.UserRepository;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com._blog.myblog.model.UserStruct;

@RestController
public class LoginAPI {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    LoginAPI(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String LoginAPI() {
        return "rak f Login abana";
    }

    @PostMapping("/login")
    public String login(@RequestBody UserStruct user) {
        Optional<UserStruct> optionalUser = userRepository.findByMail(user.getMail());
        if (optionalUser.isEmpty()) {
            return "Mail not found";
        }

        UserStruct dbUser = optionalUser.get();
        if (!passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            return "Invalid password";
        }

        return "Login successful";
    }

}
