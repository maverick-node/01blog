package com.controller.posts;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Model.PostsStruct;
import com.Model.UserStruct;
import com.Repository.PostRepo;
import com.Repository.UserRepo;
import com.services.JwtService;

@RestController 
public class GetPosts {
    private final PostRepo postRepo;
    private final JwtService jwtService;
    private final UserRepo userRepo;
    public GetPosts(PostRepo postRepo, JwtService jwtService, UserRepo user) {
        this.jwtService = jwtService;
        this.postRepo = postRepo;
        this.userRepo = user;
    }
    @GetMapping("/get-posts")
    public ResponseEntity<List<PostsStruct>> getPosts() {
        
        List<PostsStruct> posts = postRepo.findAll();
        System.out.println(posts);
        return ResponseEntity.ok(posts);
    }

    //get my posts and followed users posts
    @GetMapping("/get-followed-posts")
    public ResponseEntity<List<PostsStruct>> getFollowedPosts(@CookieValue("jwt") String jwt) {
        String username = jwtService.extractUsername(jwt);

        UserStruct user = userRepo.findByUsername(username);
        List<PostsStruct> posts = postRepo.findPostsOfFollowedUsers(user.getId());
        System.out.println(posts);
        return ResponseEntity.ok(posts);
    }
    @GetMapping("/get-my-posts")
    public ResponseEntity<List<PostsStruct>> getMyPosts(@CookieValue("jwt") String jwt) {
        String username = jwtService.extractUsername(jwt);
        UserStruct user = userRepo.findByUsername(username);
        List<PostsStruct> posts = postRepo.findAllByAuthor(user.getUsername());
       
        return ResponseEntity.ok(posts);
    }
    }