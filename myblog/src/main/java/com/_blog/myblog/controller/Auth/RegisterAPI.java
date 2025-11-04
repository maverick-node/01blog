package com._blog.myblog.controller.Auth;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;

import com._blog.myblog.model.UserStruct;
import com._blog.myblog.repository.UserRepository;
import com._blog.myblog.services.JwtService;
import com._blog.myblog.services.UserService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping
@CrossOrigin(origins = "http://localhost:4200") 
public class RegisterAPI {

    private final UserService userService;
    private final UserRepository userRepository;


    public RegisterAPI(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;

    }
 

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody UserStruct user) {
        System.out.println(user.getPassword());
        // Check if email already exists
        if (userRepository.findByMail(user.getMail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message","mail used"));
        }
        // Check password length
        if (user.getPassword() == null || user.getPassword().length() < 3) {
            
               return ResponseEntity.badRequest().body(Map.of("message", "Error: Password too short"));
        }
        if (user.getAge()<18){
              
              return ResponseEntity.badRequest().body(Map.of("message", "Error: You must be +18"));
              
        }
        if (user.getUsername().length()<3){
             return ResponseEntity.badRequest().body(Map.of("message","Error: The username must be more than 3 Characters"));
        }
        if (userRepository.existsByUsername(user.getUsername())) {
              return ResponseEntity.badRequest().body(Map.of("message","Username already exists"));
    }

 
        userService.registerUser(user.getMail(), user.getPassword(), user.getUsername(), user.getBio(),user.getAge());

        
     

          return ResponseEntity.ok(Map.of("message", "Register Succesful"));
    }
}
