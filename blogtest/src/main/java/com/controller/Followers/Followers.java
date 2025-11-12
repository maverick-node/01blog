package com.controller.Followers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Model.FollowersStruct;

import com.Repository.FollowersRepo;
import com.Repository.UserRepo;

import com.services.JwtService;
import com.services.NotificationService;


@RestController
public class Followers {
    private final JwtService jwtService;

    private final UserRepo userRepo;
    private final FollowersRepo followersRepo;
    private final NotificationService notificationService;

    public Followers(JwtService jwtService, UserRepo userRepo, FollowersRepo followersRepo, NotificationService notificationService) {
        this.jwtService = jwtService;

        this.userRepo = userRepo;
        this.followersRepo = followersRepo;
        this.notificationService = notificationService; 
    }
    @PostMapping("/follow-user/{username}")
    public ResponseEntity<Map<String, String>> followUser(@PathVariable String username, @CookieValue("jwt") String jwt) {
        String currentUser = jwtService.extractUsername(jwt);
        if (userRepo.existsByUsername(username) == false) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid JWT token!"));
        }
        int userId = userRepo.findByUsername(currentUser).getId();
        int followUserId = userRepo.findByUsername(username).getId();
        if (userId == followUserId) {
            return ResponseEntity.badRequest().body(Map.of("error", "You cannot follow yourself!"));
        }

        FollowersStruct dbFollowersStruct = new FollowersStruct();
        dbFollowersStruct.setSubscriberId(userId);
        dbFollowersStruct.setTargetId(followUserId);
        followersRepo.save(dbFollowersStruct);
         //notification can be added here
        notificationService.createNotification(
                followUserId,
                userId,
                "follow",
                "New follower added");
        return ResponseEntity.ok(Map.of("message", "User followed successfully"));
    }

}
