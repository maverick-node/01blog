package com.services;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.Exceptions.*;
import com.Model.*;
import com.Repository.*;
import com.dto.CreatePostDTO;
import com.services.*;;

@Service
@Transactional
public class PostService {

    private static final String UPLOAD_DIR = "uploads/";

    private final PostRepo postRepo;
    private final UserRepo userRepo;
    private final NotificationRepo notificationRepo;
    private final JwtService jwtService;
    private final FileRepo fileRepo;
    private final IsFollowingService isFollowingService;

    public PostService(PostRepo postRepo,
            UserRepo userRepo,
            NotificationRepo notificationRepo,
            JwtService jwtService,
            FileRepo fileRepo, IsFollowingService isFollowingService) {
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.notificationRepo = notificationRepo;
        this.jwtService = jwtService;
        this.fileRepo = fileRepo;
        this.isFollowingService = isFollowingService;
    }

    // ===================== CREATE POST (MULTIPLE FILES) =====================
    public void createPost(CreatePostDTO dto, MultipartFile[] mediaFiles, String jwt) {
        String username = jwtService.extractUsername(jwt);
        UserStruct user = userRepo.findByUsername(username);

        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new InvalidPostException("Content is required");
        }
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new InvalidPostException("Title is required");
        }
        if (user.isBanned()) {
            throw new UnauthorizedActionException("You are banned from creating posts");
        }

        PostsStruct post = new PostsStruct();
        post.setAuthorUser(user);
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        // Handle multiple media files
        if (mediaFiles != null && mediaFiles.length > 0) {
            for (MultipartFile file : mediaFiles) {
                if (!file.isEmpty()) {
                    saveFileAndLink(post, file);
                }
            }
        }

        postRepo.save(post);

        // get all users
        List<UserStruct> allusers = userRepo.findAll();

        for (UserStruct u : allusers) {
            if (isFollowingService.CheckIfFollow(u.getUsername(), user.getUsername())) { // u follows creator
                NotificationStruct notif = new NotificationStruct();
                notif.setUser(u); // recipient
                notif.setFromUser(user); // creator
                notif.setType("post");
                notif.setMessage("New post created by " + user.getUsername());
                notif.setCreatedAt(LocalDateTime.now());
                notificationRepo.save(notif);
            }
        }

    }

    // ===================== UPDATE POST (MULTIPLE FILES + REMOVE)
    // =====================
    public PostsStruct updatePostWithMultipleMedia(
            Integer postId,
            String title,
            String content,
            MultipartFile[] newFiles,
            List<Integer> removeMediaIds,
            String jwt) {

        String username = jwtService.extractUsername(jwt);
        UserStruct currentUser = userRepo.findByUsername(username);
     

        if (currentUser.isBanned()) {
            throw new UnauthorizedActionException("You are banned");
        }

        PostsStruct post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        if (!post.getAuthorUser().getUsername().equals(username)) {
            throw new UnauthorizedActionException("You can only edit your own posts");
        }
        if (post.isHidden()) {
            throw new UnauthorizedActionException("You can only edit on visible post");

        }
        if (title.trim().length() > 50 || content.trim().length() > 600) {
            throw new UnauthorizedActionException("Title or content too large");
        }
        if (title == null || title.isBlank() || content == null || content.isBlank()) {
            throw new InvalidPostException("Title and content cannot be empty");
        }
        
        // Update text
        post.setTitle(title);
        post.setContent(content);

        // Remove selected files
        if (removeMediaIds != null && !removeMediaIds.isEmpty()) {
            post.getMediaFiles().removeIf(file -> {
                if (removeMediaIds.contains(file.getId())) {
                    deleteFileFromDisk(file.getFilePath());
                    return true;
                }
                return false;
            });
        }

        // Add new files
        if (newFiles != null && newFiles.length > 0) {

            for (MultipartFile file : newFiles) {

                if (!file.isEmpty()) {
                    saveFileAndLink(post, file);
                }
            }
        }

        return postRepo.save(post);
    }

    // ===================== DELETE POST =====================
    public void deletePost(Integer postId, String jwt) {
        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty()) {
            throw new InvalidJwtTokenException("Invalid JWT token");
        }
        
        UserStruct user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        if (user.isBanned()) {
            throw new UnauthorizedActionException("You are banned");
        }

        PostsStruct post = postRepo.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found"));

        if (!post.getAuthorUser().getUsername().equals(username)) {
            throw new UnauthorizedActionException("Not your post");
        }
        if (post.isHidden()) {
            throw new UnauthorizedActionException("Your post is hidden");

        }
        // Delete all media files from disk
        if (post.getMediaFiles() != null) {
            post.getMediaFiles().forEach(file -> deleteFileFromDisk(file.getFilePath()));
        }

        postRepo.delete(post);
    }

    // ===================== HELPER: Save file & link to post =====================
    private void saveFileAndLink(PostsStruct post, MultipartFile file) {
        try {
            Path uploadDir = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            FileStruct fileStruct = new FileStruct();
            fileStruct.setFileName(file.getOriginalFilename());
            fileStruct.setFilePath("/uploads/" + filename);
            fileStruct.setFileType(file.getContentType());
            fileStruct.setFileSize(file.getSize());
            fileStruct.setPost(post);

            post.addMediaFile(fileStruct); // uses helper method

        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + e.getMessage());
        }
    }

    // ===================== HELPER: Delete file from disk =====================
    private void deleteFileFromDisk(String filePath) {
        if (filePath != null && !filePath.isBlank()) {
            try {
                Path path = Paths.get(filePath.replaceFirst("^/uploads/", UPLOAD_DIR));
                Files.deleteIfExists(path);
            } catch (IOException e) {
                System.err.println("Failed to delete file: " + filePath);
            }
        }
    }

    // ===================== BAN CHECK =====================
    private boolean isBanned(String username) {
        UserStruct userrepo = userRepo.findByUsername(username);
        if (userrepo.isBanned()) {
            return true;
        }
        return false;

    }
}