package com.services;

import org.springframework.stereotype.Service;

import com.Exceptions.InvalidJwtTokenException;
import com.Exceptions.InvalidPostException;
import com.Exceptions.PostNotFoundException;
import com.Exceptions.UnauthorizedActionException;
import com.Model.NotificationStruct;
import com.Model.PostsStruct;
import com.Repository.NotificationRepo;
import com.Repository.PostRepo;
import com.Repository.UserRepo;
import com.dto.CreatePostDTO;
import com.Model.UserStruct;
@Service
public class PostService {

    private final PostRepo postRepo;
    private final UserRepo userRepo;
    private final NotificationRepo notificationRepo;
    private final JwtService jwtService;

    public PostService(PostRepo postRepo, UserRepo userRepo, NotificationRepo notificationRepo, JwtService jwtService) {
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.notificationRepo = notificationRepo;
        this.jwtService = jwtService;
    }

    public void createPost(CreatePostDTO dto, String jwt) {

        String username = jwtService.extractUsername(jwt);
        if (username == null) {
            throw new InvalidPostException("Invalid JWT token");
        }

        UserStruct user = userRepo.findByUsername(username);
        if (user == null) {
            throw new InvalidPostException("User not found");
        }
        if (dto.getContent() == null || dto.getContent().isEmpty()) {
            throw new InvalidPostException("Content is required");
        }
        if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
            throw new InvalidPostException("Title is required");
        }
        PostsStruct post = new PostsStruct();
        post.setAuthor(username);
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        postRepo.save(post);

        // Notification (only if needed)
        NotificationStruct notif = new NotificationStruct();
        notif.setUserId(user.getId());
        notif.setFromUserId(user.getId());
        notif.setType("post");
        notif.setMessage("New post created");

        notificationRepo.save(notif);
    }

    //****************.   Delete Post.  *****************/
      public void deletePost(Integer postId, String jwt) {
        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty()) {
            throw new InvalidJwtTokenException("Invalid JWT token");
        }

        var postOpt = postRepo.findById(postId);
        if (postOpt.isEmpty()) {
            throw new PostNotFoundException("Post not found");
        }

        var post = postOpt.get();
        if (!post.getAuthor().equals(username)) {
            throw new UnauthorizedActionException("You are not the author of this post");
        }

        postRepo.deleteById(postId);
    }
    //****************.   Update Post.  *****************/
     public PostsStruct updatePost(Integer postId, PostsStruct newContent, String jwt) {
        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty()) {
            throw new InvalidJwtTokenException("Invalid JWT token");
        }

        PostsStruct post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        if (!post.getAuthor().equals(username)) {
            throw new UnauthorizedActionException("You are not the author of this post");
        }
        post.setId(postId);
        post.setTitle(newContent.getTitle());
        post.setContent(newContent.getContent());

        return postRepo.save(post);
    }
}
