package com.controller.posts;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Model.PostsStruct;
import com.Repository.PostRepo;

@RestController 
public class GetPosts {
    private final PostRepo postRepo;
    public GetPosts(PostRepo postRepo) {
        this.postRepo = postRepo;
    }
    @GetMapping("/get-posts")
    public ResponseEntity<List<PostsStruct>> getPosts() {
        
        List<PostsStruct> posts = postRepo.findAll();
        System.out.println(posts);
        return ResponseEntity.ok(posts);
    }
}
