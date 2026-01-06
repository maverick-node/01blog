package com.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Model.LikesStruct;

import jakarta.persistence.LockModeType;

import java.util.List;


public interface LikesRepo extends JpaRepository<LikesStruct, Long>{
    List<LikesStruct> findByPostId(Integer postId);
    LikesStruct findByPostIdAndUserId(int i, int q);
    boolean  existsById(Integer id);
List<LikesStruct> findAllByUserIdAndLikedTrue(int userId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT l FROM LikesStruct l
        WHERE l.post.id = :postId AND l.user.id = :userId
    """)
    LikesStruct findByPostIdAndUserIdForUpdate(
        @Param("postId") Integer postId,
        @Param("userId") Integer userId
    );

}
