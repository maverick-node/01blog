package com.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Model.UserStruct;
import com.dto.UserDTOMiddle;

public interface UserRepo extends JpaRepository<UserStruct, Integer> {
    boolean existsByMail(String mail);
    boolean existsByUsername(String username);
    UserStruct findByUsername(String username);
    UserStruct getUserByUsername(String username);
    void deleteByUsername(String username);
    UserStruct findByMail(String email);
}
