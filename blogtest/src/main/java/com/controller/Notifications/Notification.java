package com.controller.Notifications;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import com.Repository.NotificationRepo;
import com.Repository.UserRepo;
import com.services.JwtService;

public class Notification {
    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final NotificationRepo notificationRepo;

    public Notification(JwtService jwtService, NotificationRepo notificationRepo, UserRepo userRepo) {
        this.jwtService = jwtService;
        this.notificationRepo = notificationRepo;
        this.userRepo = userRepo;
    }

    @GetMapping("get-notifcation")
    public ResponseEntity<Map<String, String>> getNotifications(@CookieValue("jwt") String jwt) {
        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Invalid JWT token!"));
        }
        Integer userId = userRepo.findByUsername(username).getId();
        return ResponseEntity.ok(Map.of("notifications", notificationRepo.findById(userId).toString()));
    }

    @GetMapping("mark-as-read")
    public ResponseEntity<Map<String, String>> markAsRead(@CookieValue("jwt") String jwt) {
        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Invalid JWT token!"));
        }
        Integer userId = userRepo.findByUsername(username).getId();
        notificationRepo.markAsRead(userId);
        return ResponseEntity.ok(Map.of("message", "Notifications marked as read"));

    }
}
