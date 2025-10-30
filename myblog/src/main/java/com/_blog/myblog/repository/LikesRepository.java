package com._blog.myblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com._blog.myblog.model.LikesStruct;

public interface LikesRepository extends JpaRepository<LikesStruct, Integer> { 
    LikesStruct findByPostIdAndUserId(int postid,int id);
}
