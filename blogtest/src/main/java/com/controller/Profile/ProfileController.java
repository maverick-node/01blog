package com.controller.Profile;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dto.UserProfileDTO;

import com.services.ProfileService;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<UserProfileDTO> getProfile(
            @PathVariable String username,
            @CookieValue("jwt") String jwt) {
        System.out.println("Get profile request for user: " + username);

        UserProfileDTO profile = profileService.getProfile(username, jwt);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/editmyinfo")
    public ResponseEntity<Map<String, String>> editmyinfo(@RequestBody UserProfileDTO info,
            @CookieValue("jwt") String jwt) {
        return profileService.editmyinfo(info, jwt);
    }
}
