package com.Repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.Model.UserStruct;

public interface UserRepo extends JpaRepository<UserStruct, Integer> {
    boolean existsByMail(String mail);

    boolean existsByUsername(String username);

    UserStruct findByUsername(String username);

    UserStruct findByMail(String mail);

    UserStruct getUserByUsername(String username);

    void deleteByUsername(String username);


}
