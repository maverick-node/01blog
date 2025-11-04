package com._blog.myblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com._blog.myblog.model.PostStruct;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<PostStruct, Integer> { 
    @SuppressWarnings("null")
    List<PostStruct> findAll();
    Optional<PostStruct> findById(int id);
    boolean existsById(int id);
    List<PostStruct> findByAuthor(String author);
    
}