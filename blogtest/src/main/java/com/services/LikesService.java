package com.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Exceptions.ForbiddenException;
import com.Exceptions.InvalidJwtTokenException;
import com.Exceptions.PostNotFoundException;
import com.Exceptions.UserNotFoundException;
import com.Model.LikesStruct;
import com.Model.NotificationStruct;
import com.Model.PostsStruct;
import com.Model.UserStruct;
import com.Repository.LikesRepo;
import com.Repository.NotificationRepo;
import com.Repository.PostRepo;
import com.Repository.UserRepo;
import com.dto.LikeResponseDTO;

@Service
public class LikesService {

    private final LikesRepo likesRepo;
    private final PostRepo postRepo;
    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final IsFollowingService isFollowingService;
    private final NotificationRepo notificationRepo;
    private final Map<String, Object> locks = new ConcurrentHashMap<>();

    public LikesService(LikesRepo likesRepo, PostRepo postRepo, UserRepo userRepo, JwtService jwtService,
            IsFollowingService isFollowingService, NotificationRepo notificationRepo) {
        this.likesRepo = likesRepo;
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.isFollowingService = isFollowingService;
        this.notificationRepo = notificationRepo;
    }

    @Transactional
    public Boolean toggleLike(Integer postId, String jwt) {

        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty())
            throw new InvalidJwtTokenException("Invalid JWT");

        UserStruct user = userRepo.findByUsername(username);
        if (user == null)
            throw new UserNotFoundException("User not found");

        PostsStruct post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));
        if (post.isHidden()) {
            throw new PostNotFoundException("Post not found");
        }

        // 🔒 Create a unique lock key for this user + post
        String key = postId + ":" + username;
        Object lock = locks.computeIfAbsent(key, k -> new Object());
        Boolean check = isFollowingService.CheckIfFollow(username, post.getAuthorUser().getUsername());
        if (!check && !username.equalsIgnoreCase(post.getAuthorUser().getUsername())) {
            throw new ForbiddenException("You are not following this user");

        }
        synchronized (lock) {
            // Now only one thread can toggle this like at a time
            LikesStruct existingLike = likesRepo.findByPostIdAndUserId(postId, user.getId());
            // check if follow

            boolean liked;

            if (existingLike != null) {
                liked = !existingLike.getLiked();
                existingLike.setLiked(liked);
                likesRepo.save(existingLike);
            } else {
                liked = true;
                LikesStruct newLike = new LikesStruct();
                newLike.setLiked(true);
                newLike.setPost(post);
                newLike.setUser(user);

                try {
                    likesRepo.save(newLike);
                } catch (DataIntegrityViolationException e) {
                    // Race condition fallback
                    existingLike = likesRepo.findByPostIdAndUserId(postId, user.getId());
                    liked = existingLike != null ? existingLike.getLiked() : true;
                }

                // Get all users
                List<UserStruct> allUsers = userRepo.findAll();

                // Loop through each user
                for (UserStruct follower : allUsers) {

                    // Skip notifying the user themselves
                    if (follower.getId().equals(user.getId())) {
                        continue;
                    }

                    // Check if the follower actually follows the user
                    boolean isFollowing = isFollowingService.CheckIfFollow(user.getUsername(), follower.getUsername());
                    if (!isFollowing) {

                        continue;
                    }

                    // Create the notification
                    NotificationStruct notif = new NotificationStruct();
                    notif.setUser(follower); // The user who will receive the notification
                    notif.setFromUser(user); // The user who performed the action
                    notif.setType("Like"); // Type of notification
                    notif.setMessage("New Like received"); // Notification message
                    notif.setCreatedAt(LocalDateTime.now());

                    // Save to the database
                    notificationRepo.save(notif);
                }

            }
            locks.remove(key);

            return liked;
        }
    }

    public int getLikeCount(Integer postId, String jwt) {
        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty())
            throw new InvalidJwtTokenException("Invalid JWT");

        var user = userRepo.findByUsername(username);
        if (user == null)
            throw new UserNotFoundException("User not found");

        postRepo.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found"));
        List<LikesStruct> likes = likesRepo.findByPostId(postId);
        long likeCount = likes.stream().filter(LikesStruct::getLiked).count();
        return (int) likeCount;
    }
}
