package com._blog.myblog.controller.Profile;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com._blog.myblog.repository.UserRepository;
@RestController 
public class AllUsers {
    private final UserRepository userRepository;
    public AllUsers(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @GetMapping("/all-users")
    public ResponseEntity<Map<String, List<String>>> getAllUsers() {
        List<String> users = userRepository.findAllUsernames();
        return ResponseEntity.ok(Map.of("users", users));
    }

}
