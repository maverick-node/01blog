package com.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.Exceptions.BadRequestException;
import com.Exceptions.CommentNotFoundException;
import com.Exceptions.ForbiddenException;
import com.Exceptions.InvalidJwtTokenException;
import com.Exceptions.NotFoundException;
import com.Exceptions.UnauthorizedActionException;
import com.Model.CommentStruct;
import com.Model.NotificationStruct;
import com.Model.UserStruct;
import com.Repository.CommentRepo;
import com.Repository.NotificationRepo;
import com.Repository.PostRepo;
import com.Repository.UserRepo;
import com.dto.CreateCommentDTO;
import com.dto.UpdateCommentDTO;

@Service
public class CommentService {
    private final PostRepo postRepo;
    private final CommentRepo commentRepo;
    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final IsFollowingService isFollowingService;
    private final NotificationRepo notificationRepo;

    public CommentService(PostRepo postRepo, CommentRepo commentRepo, JwtService jwtService, UserRepo userRepo,
            IsFollowingService isFollowingService, NotificationRepo notificationRepo) {
        this.postRepo = postRepo;
        this.commentRepo = commentRepo;
        this.jwtService = jwtService;
        this.userRepo = userRepo;
        this.isFollowingService = isFollowingService;
        this.notificationRepo = notificationRepo;
    }

    public void deleteComment(Integer commentId, String jwt) {

        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty()) {
            throw new RuntimeException("Invalid JWT token");
        }
        if (isBanned(username)) {
            throw new UnauthorizedActionException("You are banned from commenting");
        }
        var comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getAuthorUser().getUsername().equals(username)) {
            throw new RuntimeException("You are not the author of this comment");
        }
        if (comment.getPost().isHidden()) {
            throw new ForbiddenException("You cant delete comment on hidden post");

        }
        commentRepo.delete(comment);
    }

    public void updateComment(UpdateCommentDTO dto, String jwt) {
        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty()) {
            throw new InvalidJwtTokenException("Invalid JWT token");
        }
        if (isBanned(username)) {
            throw new UnauthorizedActionException("You are banned from commenting");
        }
        CommentStruct comment = commentRepo.findById(dto.getCommentId())
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));

        if (!comment.getAuthorUser().getUsername().equals(username)) {
            throw new UnauthorizedActionException("You are not the author of this comment");
        }
        comment.setComment(dto.getNewContent());
        commentRepo.save(comment);
    }

    public void createComment(CreateCommentDTO dto, String jwt) {
        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty()) {
            throw new InvalidJwtTokenException("Invalid JWT token");
        }
        if (isBanned(username)) {
            throw new UnauthorizedActionException("You are banned from commenting");
        }

        if (!postRepo.existsById(dto.getPostId())) {
            throw new NotFoundException("Post not found");
        }
        if (postRepo.findById(dto.getPostId()).get().isHidden()) {
            throw new UnauthorizedActionException("Cannot comment on a hidden post");
        }
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new BadRequestException("Comment content cannot be empty");
        }
        if (dto.getContent().length() > 500) {
            throw new BadRequestException("Comment content exceeds maximum length of 500 characters");
        }
        var find = postRepo.findById(dto.getPostId());
        boolean check = isFollowingService.CheckIfFollow(username, find.get().getAuthorUser().getUsername());
        if (!check && !username.equalsIgnoreCase(find.get().getAuthorUser().getUsername())){
            throw new ForbiddenException("You are not following this user");
        }
        CommentStruct comment = new CommentStruct();
        comment.setPost(postRepo.findById(dto.getPostId()).orElseThrow(() -> new RuntimeException("Post not found")));
        comment.setAuthorUser(userRepo.findByUsername(username));
        comment.setComment(dto.getContent());
        comment.setCreatedAt(dto.getCreatedAt());

        commentRepo.save(comment);

        List<UserStruct> allUsers = userRepo.findAll();

        for (UserStruct u : allUsers) {
            // check if i commented on my own dont send notification
            if (u.getUsername().equalsIgnoreCase(username)) {
                continue;
            }

            if (isFollowingService.CheckIfFollow(username, u.getUsername())) { // u follows commenter
                // check if he comment on his own post dont notify
                if (postRepo.findById(dto.getPostId()).get().getAuthorUser().getUsername()
                        .equalsIgnoreCase(username)) {
                    continue;
                }
                NotificationStruct notif = new NotificationStruct();
                notif.setUser(u); // recipient
                notif.setFromUser(comment.getAuthorUser()); // commenter
                notif.setType("comment");
                notif.setMessage("New comment created by " + username);
                notif.setCreatedAt(LocalDateTime.now());
                notificationRepo.save(notif);
            }
        }
        return;

    }

    private boolean isBanned(String username) {
        var user = userRepo.findByUsername(username);
        if (user.isBanned()) {
            return true;
        }
        return false;
    }
}
