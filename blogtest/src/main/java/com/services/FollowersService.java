package com.services;

import org.springframework.stereotype.Service;

import com.Exceptions.InvalidJwtTokenException;
import com.Exceptions.UnauthorizedActionException;
import com.Exceptions.UserNotFoundException;
import com.Model.FollowersStruct;
import com.Model.NotificationStruct;
import com.Repository.FollowersRepo;
import com.Repository.NotificationRepo;
import com.Repository.UserRepo;

@Service
public class FollowersService {

    private final UserRepo userRepo;
    private final FollowersRepo followersRepo;
    private final NotificationRepo notificationRepo;
    private final JwtService jwtService;

    public FollowersService(UserRepo userRepo, FollowersRepo followersRepo,
            NotificationRepo notificationRepo, JwtService jwtService) {
        this.userRepo = userRepo;
        this.followersRepo = followersRepo;
        this.notificationRepo = notificationRepo;
        this.jwtService = jwtService;
    }

    public String followUser(String targetUsername, String jwt) {

        String currentUsername = jwtService.extractUsername(jwt);
        if (currentUsername == null || currentUsername.isEmpty()) {
            throw new InvalidJwtTokenException("Invalid JWT token");
        }

        var currentUser = userRepo.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new InvalidJwtTokenException("Current user not found");
        }
        if (currentUser.isBanned()) {
            throw new UnauthorizedActionException("You are banned from following users");
        }
        var targetUser = userRepo.findByUsername(targetUsername);
        if (targetUser == null) {
            throw new UserNotFoundException("User to follow does not exist");
        }

        if (currentUser.getId() == targetUser.getId()) {
            throw new UnauthorizedActionException("You cannot follow yourself");
        }
        if (followersRepo.existsBySubscriberIdAndTargetId(currentUser.getId(), targetUser.getId())) {
            throw new UnauthorizedActionException("You are already following this user");
            
        }
        // Save follower
        FollowersStruct follower = new FollowersStruct();
        follower.setSubscriber(currentUser);
        follower.setTarget(targetUser);
        followersRepo.save(follower);

        // Save notification
        NotificationStruct notification = new NotificationStruct();
        notification.setUser(targetUser);
        notification.setFromUser(currentUser);
        notification.setType("follow");
        notification.setMessage("New follower added");
        notificationRepo.save(notification);
        return "Followed successfully";
    }

    public boolean isFollowing(int subscriberId, int targetId) {
        return followersRepo.existsBySubscriberIdAndTargetId(subscriberId, targetId);
    }


    public String unfollowUser(String targetUsername, String jwt) {



        String currentUsername = jwtService.extractUsername(jwt);
        if (currentUsername == null || currentUsername.isEmpty()) {
            throw new InvalidJwtTokenException("Invalid JWT token");
        }
        
        var currentUser = userRepo.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new InvalidJwtTokenException("Current user not found");
        }

        var targetUser = userRepo.findByUsername(targetUsername);
        if (targetUser == null) {
            throw new UserNotFoundException("User to unfollow does not exist");
        }
       if (currentUser.getId() == targetUser.getId()) {
            throw new UnauthorizedActionException("You cannot unfollow yourself");
        }
        if (currentUser.isBanned()) {
            throw new UnauthorizedActionException("You are banned from unfollowing users");
        }   
        if (!followersRepo.existsBySubscriberIdAndTargetId(currentUser.getId(), targetUser.getId())) {
            throw new UnauthorizedActionException("You are not following this user");
        }
        FollowersStruct follower = followersRepo.findBySubscriberIdAndTargetId(currentUser.getId(), targetUser.getId());
      
        if (follower == null) {
            throw new UnauthorizedActionException("You are not following this user");
        }

        followersRepo.delete(follower);
        return "Unfollowed successfully";
    }
}
