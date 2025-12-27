package com.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.Exceptions.ReportNotFoundException;
import com.Exceptions.UnauthorizedActionException;
import com.Model.PostsStruct;
import com.Model.ReportStruct;
import com.Model.UserStruct;
import com.Repository.PostRepo;
import com.Repository.ReportRepo;
import com.Repository.UserRepo;
import com.dto.ReportedDTO;

import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final PostRepo postRepo;
    private final ReportRepo reportRepo;
    private final com.Repository.NotificationRepo notificationRepository;

    public AdminService(JwtService jwtService, UserRepo userRepo, PostRepo postRepo, ReportRepo reportRepo,
            com.Repository.NotificationRepo notificationRepository) {
        this.jwtService = jwtService;
        this.userRepo = userRepo;
        this.postRepo = postRepo;
        this.reportRepo = reportRepo;
        this.notificationRepository = notificationRepository;

    }

    public void checkAdmin(String jwt) {
        String username = jwtService.extractUsername(jwt);
        UserStruct user = userRepo.findByUsername(username);
        if (user == null || !user.getRole().toLowerCase().equals("admin")) {
            throw new UnauthorizedActionException("You are not an admin!");
        }
    }

    public List<ReportedDTO> getAllReportsNotResolved(String jwt) {
        checkAdmin(jwt);
        List<ReportedDTO> dtos = new ArrayList<>();
        List<ReportStruct> reports = reportRepo.findAll();
        if (reports.isEmpty()) {
            return dtos;
        }
        System.out.println(reports);
        for (ReportStruct r : reports) {
            if (r.isResolved()) {
                continue;
            }
            ReportedDTO dto = new ReportedDTO();
            dto.setId(r.getId());
            dto.setReason(r.getReason());

            // Reporter
            if (r.getReporter() != null) {
                dto.setReporterName(
                        userRepo.findById(r.getReporter().getId())
                                .map(UserStruct::getUsername)
                                .orElse("Unknown"));
            } else {
                dto.setReporterName("Unknown");
            }

            // Target User
            if (r.getTargetUser() != null) {
                dto.setTargetUserName(r.getTargetUser().getUsername());
            } else {
                dto.setTargetUserName("Unknown");
            }

            // Post
            dto.setReportedPostId(
                    r.getReportedPost() != null ? r.getReportedPost().getId() : 0);

            dto.setCreatedAt(r.getCreatedAt());

            dtos.add(dto);
        }
        return dtos;
    }

    public void resolveReport(String jwt, int reportId) {
        checkAdmin(jwt);
        ReportStruct report = reportRepo.findById(reportId);
        if (report == null) {
            throw new ReportNotFoundException("Report not found");
        }
        report.setResolved(true);
        report.setCreatedAt(LocalDateTime.now());
        reportRepo.save(report);
    }

    @Transactional
    public void deleteUser(String jwt, String username) {
        checkAdmin(jwt);
        if (!userRepo.existsByUsername(username)) {
            throw new com.Exceptions.UserNotFoundException("User not found");
        }

        userRepo.deleteByUsername(username);
    }

    public void deletePost(String jwt, Integer postId) {
        checkAdmin(jwt);

        // Check if the post exists
        if (!postRepo.existsById(postId)) {
            throw new ReportNotFoundException("Post not found");
        }

        ReportStruct reports = reportRepo.findByReportedPostId(postId);

        if (reports != null) {

            reports.setResolved(true); 
            reports.setCreatedAt(LocalDateTime.now()); 

            reportRepo.save(reports);
        }
        
        postRepo.deleteById(postId);

    }

    public List<ReportedDTO> getAllReportsResolved(String jwt) {
        checkAdmin(jwt);
        List<ReportedDTO> dtos = new ArrayList<>();

        List<ReportStruct> reports = reportRepo.findAll();

        if (reports.isEmpty()) {
            return dtos;
        }
        for (ReportStruct r : reports) {
            if (!r.isResolved()) {
                continue;
            }
            ReportedDTO dto = new ReportedDTO();
            dto.setId(r.getId());
            dto.setReason(r.getReason());

            // Reporter
            if (r.getReporter() != null) {
                dto.setReporterName(
                        userRepo.findById(r.getReporter().getId())
                                .map(UserStruct::getUsername)
                                .orElse("Unknown"));
            } else {
                dto.setReporterName("Unknown");
            }

            // Target User
            if (r.getTargetUser() != null) {
                dto.setTargetUserName(r.getTargetUser().getUsername());
            } else {
                dto.setTargetUserName("Unknown");
            }

            // Post
            dto.setReportedPostId(
                    r.getReportedPost() != null ? r.getReportedPost().getId() : 0);

            dto.setCreatedAt(r.getCreatedAt());

            dtos.add(dto);
        }

        return dtos;
    }

    public void banUserOrDeban(String jwt, String username) {
        checkAdmin(jwt);
        UserStruct user = userRepo.findByUsername(username);
        if (user == null) {
            throw new com.Exceptions.UserNotFoundException("User not found");
        }
        if (user.isBanned()) {
            user.setBanned(false);
            userRepo.save(user);
            return;
        }

        user.setBanned(true);
        userRepo.save(user);
    }

    public void hidePost(String jwt, Integer postId, String reports) {
        checkAdmin(jwt);
        if (!postRepo.existsById(postId)) {
            throw new ReportNotFoundException("Post not found");
        }
        Optional<PostsStruct> optionalPost = postRepo.findById(postId);

        var solved = reportRepo.findByReportedPostId(postId);

        PostsStruct post = optionalPost.orElseThrow(() -> new RuntimeException("Post not found"));
        if (reports.equals("reports")) {
            solved.setResolved(true);
            reportRepo.save(solved);
        }
        post.setHidden(true);
        postRepo.save(post);

    }

    public void unhidePost(String jwt, Integer postId, String reports) {
        System.out.println("===========" + reports);
        checkAdmin(jwt);
        if (!postRepo.existsById(postId)) {
            throw new ReportNotFoundException("Post not found");
        }
        Optional<PostsStruct> optionalPost = postRepo.findById(postId);

        var solved = reportRepo.findByReportedPostId(postId);
        if (reports.equals("reports")) {
            solved.setResolved(true);
            reportRepo.save(solved);
        }

        PostsStruct post = optionalPost.orElseThrow(() -> new RuntimeException("Post not found"));
        post.setHidden(false);
        postRepo.save(post);

    }

}
