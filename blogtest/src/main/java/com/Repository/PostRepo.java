package com.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Model.PostsStruct;



public interface PostRepo extends JpaRepository<PostsStruct, Integer> {
    List<PostsStruct> findAll();
    Optional<PostsStruct> findById(Integer id);
    void deleteById(Integer id);
}
