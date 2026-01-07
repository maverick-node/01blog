package com.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.Model.UserStruct;

public interface UserRepo extends JpaRepository<UserStruct, Integer> {
    boolean existsByMail(String mail);

    boolean existsByUsername(String username);

    UserStruct findByUsername(String username);

    UserStruct findByMail(String mail);
    Optional<UserStruct> findByMailIgnoreCase(String mail);

    UserStruct getUserByUsername(String username);

    void deleteByUsername(String username);


}
