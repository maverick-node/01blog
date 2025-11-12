package com.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Model.CommentStruct;

public interface CommentRepo extends JpaRepository<CommentStruct, Integer> {
    
}
