package com._blog.myblog.controller.Posts;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import com._blog.myblog.model.PostStruct;
import com._blog.myblog.model.SubscriptionStruct;
import com._blog.myblog.model.UserStruct;
import com._blog.myblog.repository.SubscriptionRepository;
import com._blog.myblog.repository.UserRepository;
import com._blog.myblog.services.JwtService;
import com._blog.myblog.services.UserService;

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

    @PostMapping(value = "/create-post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> CreatePost(
            @RequestPart(name = "post") PostStruct post,
            @RequestPart(name = "file", required = false) MultipartFile file,
            @CookieValue(name = "jwt", required = false) String token) {

        try {
            String username = jwtService.extractUsername(token.replace("Bearer ", ""));

            Optional<UserStruct> optionalUser = userRepository.findByusername(username);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("message", "User not found"));
            }

            UserStruct dbUser = optionalUser.get();

            // handle optional file upload (image or video only)
            String mediaUrl = null;
            String mediaType = null;
            if (file != null && !file.isEmpty()) {
                long maxBytes = 2L * 1024L * 1024L; // 2 MB
                if (file.getSize() > maxBytes) {
                    return ResponseEntity.status(400).body(Map.of("message", "File too large. Max 2MB allowed."));
                }
                String contentType = file.getContentType();
                if (contentType == null || !(contentType.startsWith("image/") || contentType.startsWith("video/"))) {
                    return ResponseEntity.status(400).body(Map.of("message", "Invalid file type. Only images and videos are allowed."));
                }

                // save file to ./uploads with unique name
                java.nio.file.Path uploadDir = java.nio.file.Paths.get("uploads");
                if (!java.nio.file.Files.exists(uploadDir)) {
                    java.nio.file.Files.createDirectories(uploadDir);
                }

                String original = java.nio.file.Paths.get(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename()).getFileName().toString();
                String filename = System.currentTimeMillis() + "-" + java.util.UUID.randomUUID() + "-" + original;
                java.nio.file.Path destination = uploadDir.resolve(filename);
                java.nio.file.Files.copy(file.getInputStream(), destination);

                mediaUrl = "/uploads/" + filename;
                mediaType = contentType;
            }

            // persist post with optional media
            userService.savePost(username, post.getTitle(), post.getText(), mediaUrl, mediaType);

            if (!username.equals(dbUser.getUsername())) {

                List<SubscriptionStruct> subscribers = subscriptionRepository.findByTargetId(dbUser.getId());

                for (SubscriptionStruct sub : subscribers) {
                    notificationService.createNotification(
                            sub.getSubscriberId(),
                            dbUser.getId(),
                            "NEW_POST",
                            dbUser.getUsername() + " published a new post!");
                }
            }

            return ResponseEntity.ok(Map.of("message", "Post created successfully!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Failed to create post", "details", e.getMessage()));
        }
    }
}
