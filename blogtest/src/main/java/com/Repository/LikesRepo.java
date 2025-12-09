package com.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.Model.LikesStruct;
import java.util.List;


public interface LikesRepo extends JpaRepository<LikesStruct, Long>{
    List<LikesStruct> findByPostId(Integer postId);
    LikesStruct findByPostIdAndUserId(int i, int q);
    boolean  existsById(Integer id);
List<LikesStruct> findAllByUserIdAndLikedTrue(int userId);



}
