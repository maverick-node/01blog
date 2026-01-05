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
        if (targetUser.isBanned()) {
            throw new UnauthorizedActionException("You cannot report a banned user");
        }
        if (reporter.isBanned()) {
            throw new UnauthorizedActionException("You are banned from reporting users");
        }
        if (dto.getReason() == null || dto.getReason().trim().isEmpty()) {
            throw new UnauthorizedActionException("Report reason cannot be empty");
        }
        if (dto.getReason().length() > 500) {
            throw new UnauthorizedActionException("Report reason is too long");
        }

        ReportStruct report = new ReportStruct();
        report.setReporter(reporter);
        Optional<UserStruct> username = userRepo.findById(targetUser.getId());
        UserStruct getuser = username.get();
        report.setTargetUser(getuser);
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
        var reporteduser = postOwner.get().getAuthorUser().getUsername();
        if (reporterUsername.equals(reporteduser)) {
            throw new UnauthorizedActionException("You cannot report your own post");
        }
        ReportStruct report = new ReportStruct();
        report.setTargetUser(postOwner.get().getAuthorUser());
        report.setReporter(reporter);
        report.setReportedPost(postOwner.get());
        report.setReason(dto.getReason());

        reportRepo.save(report);
    }

}
