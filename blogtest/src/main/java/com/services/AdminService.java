package com.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import org.springframework.stereotype.Service;


import com.Exceptions.ReportNotFoundException;
import com.Exceptions.UnauthorizedActionException;
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

    public AdminService(JwtService jwtService, UserRepo userRepo, PostRepo postRepo, ReportRepo reportRepo) {
        this.jwtService = jwtService;
        this.userRepo = userRepo;
        this.postRepo = postRepo;
        this.reportRepo = reportRepo;

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
        List<ReportStruct> reports = reportRepo.findAll();
        System.out.println(reports);
        List<ReportedDTO> dtos = new ArrayList<>();
        for (ReportStruct r : reports) {
            if (r.isResolved()) {
                continue;
            }
            ReportedDTO dto = new ReportedDTO();
            dto.setId(r.getId());
            dto.setReason(r.getReason());

            // Reporter
            if (r.getReporterId() != null) {
                dto.setReporterName(
                        userRepo.findById(r.getReporterId())
                                .map(UserStruct::getUsername)
                                .orElse("Unknown"));
            } else {
                dto.setReporterName("Unknown");
            }

            // Target User
            if (r.getTargetUsername() != null) {
                dto.setTargetUserName(r.getTargetUsername());
            } else {
                dto.setTargetUserName("Unknown");
            }

            // Post
            dto.setReportedPostId(
                    r.getReportedPostId() != null ? r.getReportedPostId() : 0);

            dto.setCreatedAt(r.getCreatedAt());

            dtos.add(dto);
        }
        System.out.println(dtos.get(0));
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
        if (!postRepo.existsById(postId)) {
            throw new ReportNotFoundException("Post not found");
        }
        postRepo.deleteById(postId);
        var solved = reportRepo.findByReportedPostId(postId);
        solved.setResolved(true);
        solved.setCreatedAt(LocalDateTime.now());
        reportRepo.save(solved);

    }

    public List<ReportedDTO> getAllReportsResolved(String jwt) {
        checkAdmin(jwt);
        List<ReportStruct> reports = reportRepo.findAll();
        System.out.println(reports);
        List<ReportedDTO> dtos = new ArrayList<>();
        for (ReportStruct r : reports) {
            if (!r.isResolved()) {
                continue;
            }
            ReportedDTO dto = new ReportedDTO();
            dto.setId(r.getId());
            dto.setReason(r.getReason());

            // Reporter
            if (r.getReporterId() != null) {
                dto.setReporterName(
                        userRepo.findById(r.getReporterId())
                                .map(UserStruct::getUsername)
                                .orElse("Unknown"));
            } else {
                dto.setReporterName("Unknown");
            }

            // Target User
            if (r.getTargetUsername() != null) {
                dto.setTargetUserName(r.getTargetUsername());
            } else {
                dto.setTargetUserName("Unknown");
            }

            // Post
            dto.setReportedPostId(
                    r.getReportedPostId() != null ? r.getReportedPostId() : 0);

            dto.setCreatedAt(r.getCreatedAt());

            dtos.add(dto);
        }

        System.out.println(dtos.get(0));

        return dtos;
    }

}
