package com.Repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.Model.FollowersStruct;

public interface FollowersRepo extends JpaRepository<FollowersStruct, Integer> {
    boolean existsBySubscriberIdAndTargetId(int subscriberId, int targetId);
     
    FollowersStruct findBySubscriberIdAndTargetId(int subscriberId, int targetId);

}
