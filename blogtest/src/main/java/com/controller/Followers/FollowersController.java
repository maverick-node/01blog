package com.controller.Followers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Model.FollowersStruct;
import com.Model.UserStruct;
import com.Repository.UserRepo;
import com.services.FollowersService;
import com.services.JwtService;

@RestController
@RequestMapping("/followers")
public class FollowersController {
    private final JwtService jwtService;
    private final FollowersService followersService;
    private final UserRepo userRepo;

    public FollowersController(FollowersService followersService, JwtService jwtService, UserRepo userRepo) {
        this.followersService = followersService;
        this.jwtService = jwtService;
        this.userRepo = userRepo;
    }

    @PostMapping("/follow/{username}")
    public ResponseEntity<?> followUser(@PathVariable String username,
            @CookieValue("jwt") String jwt) {

         String message = followersService.followUser(username, jwt);
        return ResponseEntity.ok(Map.of("message", message));
    }
    @DeleteMapping("/unfollow/{username}")
    public ResponseEntity<?> unfollowUser(@PathVariable String username,
            @CookieValue("jwt") String jwt) {
                System.out.println("Unfollow request for user: " + username);
            String message = followersService.unfollowUser(username, jwt);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @GetMapping("/get-follow/{username}")
    // check if current user is following the user with username
    public ResponseEntity<Map<String, Boolean>> isFollowing(
            @PathVariable String username,
            @CookieValue("jwt") String jwt) {

        String currentUsername = jwtService.extractUsername(jwt);

        UserStruct userloged = userRepo.findByUsername(currentUsername);
        UserStruct usertarget = userRepo.findByUsername(username);
        System.out.println("Checking if " + userloged.getUsername() + " is following " + usertarget.getUsername());
        boolean isFollowing = followersService.isFollowing(userloged.getId(), usertarget.getId());
        System.out.println("Is following: " + isFollowing);
        return ResponseEntity.ok(Map.of("isFollowing", isFollowing));
    }

}
