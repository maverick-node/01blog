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
        boolean exists = followersRepo.existsBySubscriberIdAndTargetId(user.getId(), targ.getId());
        return exists;
    }
}
