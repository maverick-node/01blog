package com.services;


import java.util.Optional;

import org.springframework.stereotype.Service;

import com.Exceptions.InvalidJwtTokenException;
import com.Exceptions.UnauthorizedActionException;
import com.Exceptions.UserNotFoundException;
import com.Model.PostsStruct;
import com.Model.ReportStruct;
import com.Model.UserStruct;
import com.Repository.PostRepo;
import com.Repository.ReportRepo;
import com.Repository.UserRepo;
import com.dto.CreateReportDTO;

@Service
public class ReportService {

    private final ReportRepo reportRepo;
    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final PostRepo postRepo;

    public ReportService(ReportRepo reportRepo, UserRepo userRepo, JwtService jwtService, PostRepo postRepo) {
        this.reportRepo = reportRepo;
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.postRepo = postRepo;
    }

    public void createReport(String targetUsername, String jwt, CreateReportDTO dto) {
        String reporterUsername = jwtService.extractUsername(jwt);

        if (reporterUsername == null || !userRepo.existsByUsername(reporterUsername)) {
            throw new InvalidJwtTokenException("Invalid JWT token");
        }
        if (reporterUsername.equals(targetUsername)) {
            throw new UnauthorizedActionException("You cannot report yourself");
        }
        var reporter = userRepo.findByUsername(reporterUsername);
        var targetUser = userRepo.findByUsername(targetUsername);

        if (targetUser == null) {
            throw new UserNotFoundException("Target user not found");
        }

        if (reporter.getId() == targetUser.getId()) {
            throw new UnauthorizedActionException("You cannot report yourself");
        }

        ReportStruct report = new ReportStruct();
        report.setReporterId(reporter.getId());
        Optional<UserStruct> username = userRepo.findById(targetUser.getId());
        UserStruct getuser = username.get();
        report.setTargetUsername(getuser.getUsername());
        report.setReason(dto.getReason());

        reportRepo.save(report);
    }
    public void reportPost(Integer postId, String jwt, CreateReportDTO dto) {
        String reporterUsername = jwtService.extractUsername(jwt);

        if (reporterUsername == null || !userRepo.existsByUsername(reporterUsername)) {
            throw new InvalidJwtTokenException("Invalid JWT token");
        }

        var reporter = userRepo.findByUsername(reporterUsername);
        Optional<PostsStruct> postOwner = postRepo.findById(postId);
        var reporteduser = postOwner.get().getAuthor();
        if (reporterUsername.equals(reporteduser)) {
            throw new UnauthorizedActionException("You cannot report your own post");
        }
        ReportStruct report = new ReportStruct();
        report.setTargetUsername(reporteduser);
        report.setReporterId(reporter.getId());
        report.setReportedPostId(postId);
        report.setReason(dto.getReason());

        reportRepo.save(report);
    }
 
        
}
