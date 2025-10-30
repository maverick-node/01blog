package com._blog.myblog.controller.Notification;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com._blog.myblog.model.NotificationStruct;
import com._blog.myblog.model.UserStruct;
import com._blog.myblog.repository.NotificationRepository;
import com._blog.myblog.repository.UserRepository;
import com._blog.myblog.services.JwtService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    public NotificationController(NotificationRepository notificationRepository, 
                                  UserRepository userRepository, 
                                  JwtService jwtService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }


    @GetMapping
    public ResponseEntity<List<NotificationStruct>> getNotifications(
            @RequestHeader("Authorization") String token) {

        String username = jwtService.extractUsername(token.replace("Bearer ", ""));
        Optional<UserStruct> optionalUser = userRepository.findByusername(username);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        UserStruct user = optionalUser.get();
        List<NotificationStruct> notifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId());

        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Integer notificationId,
                                             @RequestHeader("Authorization") String token) {

        String username = jwtService.extractUsername(token.replace("Bearer ", ""));
        Optional<UserStruct> optionalUser = userRepository.findByusername(username);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid user");
        }

        Optional<NotificationStruct> optionalNotification = notificationRepository.findById(notificationId);
        if (optionalNotification.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        NotificationStruct notification = optionalNotification.get();
        if (!notification.getUserId().equals(optionalUser.get().getId())) {
            return ResponseEntity.status(403).body("You can only modify your own notifications");
        }

        notification.setRead(true);
        notificationRepository.save(notification);

        return ResponseEntity.ok("Notification marked as read");
    }
}
