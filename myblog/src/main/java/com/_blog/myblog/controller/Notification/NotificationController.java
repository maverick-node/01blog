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

    @GetMapping("/read")
    public ResponseEntity<String> markAsRead(@RequestHeader("Authorization") String token) {
System.out.println("mark as read called/n mark as read called");
       //set all notification read
        String username = jwtService.extractUsername(token.replace("Bearer ", ""));
        Optional<UserStruct> optionalUser = userRepository.findByusername(username);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        UserStruct user = optionalUser.get();
        List<NotificationStruct> notifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId());

        for (NotificationStruct notification : notifications) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }

        return ResponseEntity.ok("All notifications marked as read");
    }
}
