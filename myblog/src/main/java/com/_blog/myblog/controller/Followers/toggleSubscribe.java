package com._blog.myblog.controller.Followers;

import java.util.List;
import java.util.Optional;
import com._blog.myblog.services.NotificationService;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com._blog.myblog.model.SubscriptionStruct;
import com._blog.myblog.model.UserStruct;
import com._blog.myblog.repository.SubscriptionRepository;
import com._blog.myblog.repository.UserRepository;
import com._blog.myblog.services.JwtService;

@RestController
public class toggleSubscribe {

    private final NotificationService notificationService;
    private JwtService jwtService;
    private UserRepository userRepository;
    private SubscriptionRepository subscriptionRepository;

    public toggleSubscribe(JwtService jwtService, UserRepository userRepository,
            SubscriptionRepository subscriptionRepository, NotificationService notificationService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.notificationService = notificationService;
    }

    @PostMapping("/users/{targetId}/subscribe")
    public ResponseEntity<String> toggleSubscribe(@PathVariable Integer targetId,
            @RequestHeader("Authorization") String token) {

        String username = jwtService.extractUsername(token.replace("Bearer ", ""));
        Optional<UserStruct> optionalUser = userRepository.findByusername(username);
        UserStruct dbUser = optionalUser.get();

        boolean exists = subscriptionRepository.existsBySubscriberIdAndTargetId(dbUser.getId(), targetId);

        if (exists) {
            subscriptionRepository.deleteBySubscriberIdAndTargetId(dbUser.getId(), targetId);
            return ResponseEntity.ok("Unsubscribed successfully");
        }

        SubscriptionStruct s = new SubscriptionStruct();
        s.setSubscriberId(dbUser.getId());
        s.setTargetId(targetId);
        subscriptionRepository.save(s);


     List<SubscriptionStruct> subscribers = subscriptionRepository.findByTargetId(dbUser.getId());

        for (SubscriptionStruct sub : subscribers) {
            notificationService.createNotification(
                    sub.getSubscriberId(),
                    dbUser.getId(),
                    "NEW_POST",
                    dbUser.getUsername() + " published a new post!"
            );
        }

        return ResponseEntity.ok("Subscribed successfully");
    }

    @GetMapping("/users/{userId}/subscriptions")
    public List<SubscriptionStruct> getSubscriptions(@PathVariable Integer userId) {
        return subscriptionRepository.findBySubscriberId(userId);
    }

}
