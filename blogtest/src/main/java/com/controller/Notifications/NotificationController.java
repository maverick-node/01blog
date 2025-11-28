package com.controller.Notifications;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Model.NotificationStruct;

import com.dto.NotificationDTO;

import com.services.NotificationService;
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getNotifications(@CookieValue("jwt") String jwt) {
        List<NotificationStruct> notifications = notificationService.getNotifications(jwt);

        List<NotificationDTO> response = notifications.stream()
                .map(n -> new NotificationDTO(n.getId(), n.getType(), n.getMessage(), n.isRead()))
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/mark-as-read")
    public ResponseEntity<Map<String, String>> markAsRead(@CookieValue("jwt") String jwt) {
        notificationService.markAsRead(jwt);
        return ResponseEntity.ok(Map.of("message", "Notifications marked as read"));
    }
}
