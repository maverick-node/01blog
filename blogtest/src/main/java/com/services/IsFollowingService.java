package com.services;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.Model.UserStruct;
import com.Repository.FollowersRepo;
import com.Repository.UserRepo;

@Service
public class IsFollowingService {
    private final FollowersRepo followersRepo;

    private final UserRepo userRepo;

    public IsFollowingService(FollowersRepo followersRepo, UserRepo userRepo) {
        this.followersRepo = followersRepo;
        this.userRepo = userRepo;
    }

    public Boolean CheckIfFollow(String current, String target) {
        UserStruct user = userRepo.findByUsername(current);
        UserStruct targ = userRepo.findByUsername(target);

        System.out.println("Checking if '" + current + "' follows '" + target + "'");
        System.out.println("Follower ID: " + user.getId() + ", Target ID: " + targ.getId());
        boolean exists = followersRepo.existsBySubscriberIdAndTargetId(user.getId(), targ.getId());
        System.out.println("Exists? " + exists);
        return exists;
    }
}
