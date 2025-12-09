package com.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Exceptions.InvalidJwtTokenException;
import com.Exceptions.PostNotFoundException;
import com.Exceptions.UserNotFoundException;
import com.Model.LikesStruct;
import com.Repository.LikesRepo;
import com.Repository.PostRepo;
import com.Repository.UserRepo;
import com.dto.LikeResponseDTO;

@Service
public class LikesService {

    private final LikesRepo likesRepo;
    private final PostRepo postRepo;
    private final UserRepo userRepo;
    private final JwtService jwtService;

    public LikesService(LikesRepo likesRepo, PostRepo postRepo, UserRepo userRepo, JwtService jwtService) {
        this.likesRepo = likesRepo;
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.jwtService = jwtService;
    }

    public LikeResponseDTO toggleLike(Integer postId, String jwt) {

        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty())
            throw new InvalidJwtTokenException("Invalid JWT");

        var user = userRepo.findByUsername(username);
        if (user == null)
            throw new UserNotFoundException("User not found");

        postRepo.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found"));

        LikesStruct existingLike = likesRepo.findByPostIdAndUserId(postId, user.getId());
        boolean liked = true;
        if (existingLike != null) {
            liked = !existingLike.getLiked();
            existingLike.setLiked(liked);
            likesRepo.save(existingLike);
        } else {
            LikesStruct newLike = new LikesStruct();
            newLike.setLiked(true);
            newLike.setPost(postRepo.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found")));
            newLike.setUser(user);
            likesRepo.save(newLike);
        }

        return new LikeResponseDTO(postId, user.getId(), liked);
    }

    public int getLikeCount(Integer postId, String jwt) {
        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty())
            throw new InvalidJwtTokenException("Invalid JWT");

        var user = userRepo.findByUsername(username);
        if (user == null)
            throw new UserNotFoundException("User not found");

        postRepo.findById(postId).orElseThrow(() -> new PostNotFoundException("Post not found"));
        List<LikesStruct> likes = likesRepo.findByPostId(postId);
        long likeCount = likes.stream().filter(LikesStruct::getLiked).count();
        return (int) likeCount;
    }
}
