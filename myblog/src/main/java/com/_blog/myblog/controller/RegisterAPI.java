package com._blog.myblog.controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com._blog.myblog.model.UserStruct;
import com._blog.myblog.repository.UserRepository;

@RestController
@RequestMapping
public class RegisterAPI {

    private final UserService userService;
    private final UserRepository userRepository;

    public RegisterAPI(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }
    @GetMapping("/register")
    public String RegisterAPI(){
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestBody UserStruct user) {
        // Check if email already exists
        if (userRepository.findByMail(user.getMail()).isPresent()) {
            return "Error: Email has already been used";
        }
        // Check password length
        if (user.getPassword() == null || user.getPassword().length() < 3) {
            return "Error: Password too short";
        }
        if (user.getAge()<18){
              return "Error: You must be +18";
        }
        if (user.getUsername().length()<3){
              return "Error: The username must be more than 3 Characters";
        }
        if (userRepository.existsByUsername(user.getUsername())) {
        throw new RuntimeException("Username already exists");
    }
        userService.registerUser(user.getMail(), user.getPassword(), user.getUsername(), user.getBio(),user.getAge());

        return "User registered successfully";
    }
}
