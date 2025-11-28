package com.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.Model.LikesStruct;
import java.util.List;


public interface LikesRepo extends JpaRepository<LikesStruct, Long>{
    Integer countByPostId(int s);
    LikesStruct findByPostIdAndUserId(int i, int q);
    boolean  existsById(Integer id);


}
