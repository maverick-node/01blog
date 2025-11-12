package com._blog.myblog.controller.Followers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import com._blog.myblog.services.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public ResponseEntity<Map<String, String>> toggleSubscribe(@PathVariable String targetId,
            @RequestHeader("Authorization") String token) {

        String username = jwtService.extractUsername(token.replace("Bearer ", ""));
        Optional<UserStruct> optionalUser = userRepository.findByusername(username);
        UserStruct dbUser = optionalUser.get();

        Optional<UserStruct> targetUserOpt = userRepository.findByusername(targetId);
        if (targetUserOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Target user not found"));
        }
        Integer idd = targetUserOpt.get().getId();

        boolean exists = subscriptionRepository.existsBySubscriberIdAndTargetId(dbUser.getId(), idd);

        if (exists) {
            subscriptionRepository.deleteBySubscriberIdAndTargetId(dbUser.getId(), idd);
            return ResponseEntity.ok(Map.of("message", "Unsubscribed successfully"));
        }

        SubscriptionStruct s = new SubscriptionStruct();
        s.setSubscriberId(dbUser.getId());
        s.setTargetId(idd);
        subscriptionRepository.save(s);
       System.out.println("Subscriber ID: " + targetId);
       System.out.println("Target ID: " + username);
      
        if (!username.equals(targetId)) {
            List<SubscriptionStruct> subscribers = subscriptionRepository.findByTargetId(dbUser.getId());

            for (SubscriptionStruct sub : subscribers) {
                notificationService.createNotification(
                        sub.getSubscriberId(),
                        dbUser.getId(),
                        "NEW_FOLLOWER",
                        dbUser.getUsername() + " Followed you!");
            }
        }
        return ResponseEntity.ok(Map.of("message", "Subscribed successfully"));
    }

    @GetMapping("/users/{userId}/subscriptions")
    public List<SubscriptionStruct> getSubscriptions(@PathVariable String userId) {
        Optional<UserStruct> user = userRepository.findByusername(userId);
        if (user.isPresent()) {
            return subscriptionRepository.findBySubscriberId(user.get().getId());
        }
        return List.of();
    }

   

}
