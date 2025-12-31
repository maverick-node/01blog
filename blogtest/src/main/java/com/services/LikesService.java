package com.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

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

    public LikesService(LikesRepo likesRepo, PostRepo postRepo, UserRepo userRepo, JwtService jwtService,
            IsFollowingService isFollowingService, NotificationRepo notificationRepo) {
        this.likesRepo = likesRepo;
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.isFollowingService = isFollowingService;
        this.notificationRepo = notificationRepo;
    }

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

        LikesStruct existingLike = likesRepo.findByPostIdAndUserId(postId, user.getId());
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
            likesRepo.save(newLike);
            // Notify all followers of the user
            List<UserStruct> allUsers = userRepo.findAll();


            for (UserStruct follower : allUsers) {

                // skip notifying self
                if (follower.getId().equals(user.getId()))
                    continue;

                if (isFollowingService.CheckIfFollow(follower.getUsername(), user.getUsername())) {
                    NotificationStruct notif = new NotificationStruct();
                    notif.setUser(follower);
                    notif.setFromUser(user);
                    notif.setType("Like");
                    notif.setMessage("New Like received");
                    notif.setCreatedAt(LocalDateTime.now());
                    notificationRepo.save(notif);
                }
            }
        }

        return liked;
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
