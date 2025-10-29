package com._blog.myblog.controller.Posts;

import org.springframework.web.bind.annotation.RestController;

import com._blog.myblog.model.PostStruct;
import com._blog.myblog.services.JwtService;
import com._blog.myblog.services.UserService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
public class CreatePost {
    private final com._blog.myblog.services.UserService userService;
    private final JwtService jwtService;
    public CreatePost(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }
    private String username;

    @PostMapping("/create-post")
    public String CreatePost(@RequestBody PostStruct post, @RequestHeader("Authorization") String token){
        
        username = jwtService.extractUsername(token.replace("Bearer ", ""));
        System.out.println(post.getText());
      userService.savePost(username, post.getTitle(),post.getText());
      return "post cc";
    }

}
