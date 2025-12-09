package com.controller.posts;

import java.util.List;

import org.hibernate.mapping.Any;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Model.PostsStruct;
import com.Model.UserStruct;
import com.Repository.PostRepo;
import com.Repository.UserRepo;
import com.dto.PostDTO;
import com.services.AdminService;
import com.services.JwtService;

@RestController
public class GetPosts {

    private final AdminService adminService;
    private final PostRepo postRepo;
    private final JwtService jwtService;
    private final UserRepo userRepo;

    public GetPosts(PostRepo postRepo, JwtService jwtService, UserRepo userRepo, AdminService adminService) {
        this.postRepo = postRepo;
        this.jwtService = jwtService;
        this.userRepo = userRepo;
        this.adminService = adminService;
    }

    @GetMapping("/get-posts")
    public ResponseEntity<List<PostDTO>> getPosts(@CookieValue("jwt") String jwt) {
        adminService.checkAdmin(jwt);
        return ResponseEntity.ok(mapPostsToDTOs(postRepo.findAll()));
    }

    @GetMapping("/get-followed-posts")
    public ResponseEntity<List<PostDTO>> getFollowedPosts(@CookieValue("jwt") String jwt) {
        String username = jwtService.extractUsername(jwt);
        UserStruct user = userRepo.findByUsername(username);
                

        List<PostsStruct> posts = postRepo.findPostsOfFollowedUsers(user.getId());
        return ResponseEntity.ok(mapPostsToDTOs(posts));
    }

    @GetMapping("/get-my-posts")
    public ResponseEntity<List<PostDTO>> getMyPosts(@CookieValue("jwt") String jwt) {
        String username = jwtService.extractUsername(jwt);
        List<PostsStruct> posts = postRepo.findAllByAuthorUser_Username(username);
        return ResponseEntity.ok(mapPostsToDTOs(posts));
    }

    @GetMapping("/get-posts/{username}")
    public ResponseEntity<?> getuserposts(@CookieValue("jwt") String jwt ,   @PathVariable String username){

     
        UserStruct user = userRepo.findByUsername(username);
        if (user.getUsername().isEmpty() || user.getUsername().isBlank()){
            return ResponseEntity.badRequest().body("Not Found");
        }
        List<PostsStruct> qq = postRepo.findAllByAuthorUser_Username(user.getUsername());
        return ResponseEntity.ok(mapPostsToDTOs(qq));
    }


    // HELPER METHOD – works for both single and multiple media
    private List<PostDTO> mapPostsToDTOs(List<PostsStruct> posts) {
        return posts.stream().map(post -> {
            PostDTO dto = new PostDTO();
            dto.setId(post.getId());
            dto.setTitle(post.getTitle());
            dto.setContent(post.getContent());
            dto.setCreatedAt(post.getCreatedAt());
            dto.setAuthor(post.getAuthorUser().getUsername());

            // SUPPORTS MULTIPLE MEDIA FILES
            if (post.getMediaFiles() != null && !post.getMediaFiles().isEmpty()) {
                dto.setMediaPaths(post.getMediaPaths());     // List<String>
                dto.setMediaTypes(post.getMediaTypes());     // List<String>
                dto.setMediaIds(post.getMediaIds());         // List<Integer> for deletion
            } else {
                dto.setMediaPaths(List.of());
                dto.setMediaTypes(List.of());
                dto.setMediaIds(List.of());
            }

            return dto;
        }).toList();
    }



}