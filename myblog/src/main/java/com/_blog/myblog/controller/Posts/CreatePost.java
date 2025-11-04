package com._blog.myblog.controller.Posts;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com._blog.myblog.model.PostStruct;
import com._blog.myblog.model.SubscriptionStruct;
import com._blog.myblog.model.UserStruct;
import com._blog.myblog.repository.SubscriptionRepository;
import com._blog.myblog.repository.UserRepository;
import com._blog.myblog.services.JwtService;
import com._blog.myblog.services.UserService;

import io.micrometer.core.ipc.http.HttpSender.Response;

import com._blog.myblog.services.NotificationService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class CreatePost {

    private final UserService userService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationService notificationService;

    public CreatePost(JwtService jwtService,
                      UserService userService,
                      UserRepository userRepository,
                      SubscriptionRepository subscriptionRepository,
                      NotificationService notificationService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.notificationService = notificationService;
    }

    @PostMapping("/create-post")
    public ResponseEntity<Map<String, String>> CreatePost(@RequestBody PostStruct post,
                             @CookieValue(name = "jwt", required = false) String token) {


        String username = jwtService.extractUsername(token.replace("Bearer ", ""));


        Optional<UserStruct> optionalUser = userRepository.findByusername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("message", "User not found"));
        }

        UserStruct dbUser = optionalUser.get();

        post.setId(dbUser.getId());


        userService.savePost(username, post.getTitle(), post.getText());

        List<SubscriptionStruct> subscribers = subscriptionRepository.findByTargetId(dbUser.getId());

        for (SubscriptionStruct sub : subscribers) {
            notificationService.createNotification(
                    sub.getSubscriberId(),
                    dbUser.getId(),
                    "NEW_POST",
                    dbUser.getUsername() + " published a new post!"
            );
        }

        return ResponseEntity.ok(Map.of("message", "Post created successfully!"));
    }
}
