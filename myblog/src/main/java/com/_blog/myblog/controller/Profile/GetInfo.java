package com._blog.myblog.controller.Profile;

import com._blog.myblog.model.UserStruct;
import com._blog.myblog.repository.UserRepository;
import com._blog.myblog.services.JwtService;
import com._blog.myblog.services.UserService;

import java.nio.file.OpenOption;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetInfo {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    GetInfo(JwtService jwtService, UserService userService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }
@GetMapping("/profile")
    public Optional<UserStruct> GetInfo(@RequestHeader("Authorization") String token) {
        String username = jwtService.extractUsername(token.replace("Bearer ", ""));
        return userRepository.findByusername(username);
    }
}
