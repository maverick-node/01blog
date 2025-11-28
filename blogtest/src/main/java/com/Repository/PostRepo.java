package com.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Model.PostsStruct;
import com.Model.UserStruct;

public interface PostRepo extends JpaRepository<PostsStruct, Integer> {

List<PostsStruct> findAllByAuthor(String author);

    List<PostsStruct> findAll();

    Optional<PostsStruct> findById(Integer id);

    void deleteById(Integer id);

    @Query("SELECT p FROM PostsStruct p WHERE p.author IN " +
            "(SELECT u.username FROM UserStruct u JOIN FollowersStruct f ON u.id = f.targetId WHERE f.subscriberId = :userId)")
    List<PostsStruct> findPostsOfFollowedUsers(@Param("userId") Integer userId);
}
