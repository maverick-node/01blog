package com.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Model.LikesStruct;

public interface LikesRepo extends JpaRepository<LikesStruct, Long>{
    Integer countByPostId(int s);
    LikesStruct findByPostIdAndUserId(int i, int q);

}
