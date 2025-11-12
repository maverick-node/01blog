package com._blog.myblog.repository;
import com._blog.myblog.model.UserStruct;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserStruct, Integer> {

    boolean existsByUsername(String username); 
    Optional<UserStruct> findByMail(String mail);
    Optional<UserStruct> findByusername(String username);
  @Query("SELECT u.username FROM UserStruct u")
    List<String> findAllUsernames();


}


