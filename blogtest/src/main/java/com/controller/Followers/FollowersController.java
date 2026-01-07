package com.controller.Followers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
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
import com.Repository.FollowersRepo;
import com.Repository.UserRepo;
import com.services.FollowersService;
import com.services.JwtService;

@RestController
@RequestMapping("/followers")
public class FollowersController {
    private final JwtService jwtService;
    private final FollowersService followersService;
    private final UserRepo userRepo;
    private final FollowersRepo followersRepo;
    public FollowersController(FollowersService followersService, JwtService jwtService, UserRepo userRepo, FollowersRepo followersRepo) {
        this.followersService = followersService;
        this.jwtService = jwtService;
        this.userRepo = userRepo;
        this.followersRepo=followersRepo;
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
       
        boolean isFollowing = followersService.isFollowing(userloged.getId(), usertarget.getId());
       
        return ResponseEntity.ok(Map.of("isFollowing", isFollowing));
    }

    @GetMapping("/follow/count/{username}")
    public ResponseEntity<Map<String, String>> countFollowersAndFollowing(@PathVariable String username) {
        UserStruct user = userRepo.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        // Count followers (how many users follow this user)
        long followersCount = followersRepo.countByTargetId(user.getId());

        // Count following (how many users this user follows)
        long followingCount = followersRepo.countBySubscriberId(user.getId());

        Map<String, String> response = new HashMap<>();
        response.put("followers", String.valueOf(followersCount));
        response.put("following", String.valueOf(followingCount));

        return ResponseEntity.ok(response);
    }

}
