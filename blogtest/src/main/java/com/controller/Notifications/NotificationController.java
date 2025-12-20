package com.controller.Notifications;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Model.NotificationStruct;
import com.Model.UserStruct;
import com.Repository.UserRepo;
import com.dto.NotificationDTO;

import com.services.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final UserRepo userRepo;
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService, UserRepo userRepo) {
        this.notificationService = notificationService;
        this.userRepo = userRepo;
    }

    @GetMapping("/get")
    public ResponseEntity<List<NotificationDTO>> getNotifications(@CookieValue("jwt") String jwt) {
        List<NotificationStruct> notifications = notificationService.getNotifications(jwt);

        List<NotificationDTO> response = notifications.stream()
                .map(n -> {
                    String fromUsername = n.getFromUser() != null ? n.getFromUser().getUsername() : "Unknown";
                    String toUsername = n.getUser() != null ? n.getUser().getUsername() : "Unknown";

                    return new NotificationDTO(
                            fromUsername,
                            n.getId(),
                            n.getType(),
                            n.getMessage(),
                            n.isRead(),
                            n.getCreatedAt(),
                            toUsername);
                })
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/mark-as-read/{notificationId}")
    public ResponseEntity<Map<String, String>> markAsRead(@CookieValue("jwt") String jwt,
            @PathVariable("notificationId") int notificationId) {
        notificationService.markAsRead(jwt, notificationId);
        return ResponseEntity.ok(Map.of("message", "Notifications marked as read"));
    }
}
