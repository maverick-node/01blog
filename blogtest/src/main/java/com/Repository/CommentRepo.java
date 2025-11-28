package com.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Model.CommentStruct;

public interface CommentRepo extends JpaRepository<CommentStruct, Integer> {
    List<CommentStruct> findAllByPostId(int postId);
}
