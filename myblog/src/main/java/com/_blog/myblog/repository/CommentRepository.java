package com._blog.myblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com._blog.myblog.model.CommentStruct;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentStruct, Integer> { 
   List<CommentStruct> findByPostId(int postId);
   Optional<CommentStruct> findById(int id);
}