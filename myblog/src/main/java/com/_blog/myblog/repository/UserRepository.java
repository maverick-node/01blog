package com._blog.myblog.repository;
import com._blog.myblog.model.UserStruct;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserStruct, Integer> {
    boolean existsByUsername(String username); 
    Optional<UserStruct> findByMail(String mail);
}


