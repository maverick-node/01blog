package com.Repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.Model.FollowersStruct;

public interface FollowersRepo extends JpaRepository<FollowersStruct, Integer> {
    
}
