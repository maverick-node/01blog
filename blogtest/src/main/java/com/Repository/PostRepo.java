package com.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Model.PostsStruct;

public interface PostRepo extends JpaRepository<PostsStruct, Integer> {

    List<PostsStruct> findAllByAuthorUser_Username(String username);

    List<PostsStruct> findAll();

    Optional<PostsStruct> findById(Integer id);

    void deleteById(Integer id);

@Query("""
    SELECT p FROM PostsStruct p
    WHERE p.authorUser.id = :userId
       OR p.authorUser.id IN (
           SELECT f.target.id FROM FollowersStruct f WHERE f.subscriber.id = :userId
       )
""")
    List<PostsStruct> findPostsOfFollowedUsers(@Param("userId") Integer userId);
}
