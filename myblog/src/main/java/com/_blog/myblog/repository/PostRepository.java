package com._blog.myblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com._blog.myblog.model.PostStruct;

import java.util.List;

public interface PostRepository extends JpaRepository<PostStruct, Integer> { 
    @SuppressWarnings("null")
    List<PostStruct> findAll();
}