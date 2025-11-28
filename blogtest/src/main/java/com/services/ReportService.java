package com.services;

import org.springframework.stereotype.Service;

import com.Exceptions.InvalidJwtTokenException;
import com.Exceptions.UnauthorizedActionException;
import com.Exceptions.UserNotFoundException;
import com.Model.ReportStruct;
import com.Repository.ReportRepo;
import com.Repository.UserRepo;
import com.dto.CreateReportDTO;

@Service
public class ReportService {

    private final ReportRepo reportRepo;
    private final UserRepo userRepo;
    private final JwtService jwtService;

    public ReportService(ReportRepo reportRepo, UserRepo userRepo, JwtService jwtService) {
        this.reportRepo = reportRepo;
        this.userRepo = userRepo;
        this.jwtService = jwtService;
    }

    public void createReport(String targetUsername, String jwt, CreateReportDTO dto) {
        String reporterUsername = jwtService.extractUsername(jwt);

        if (reporterUsername == null || !userRepo.existsByUsername(reporterUsername)) {
            throw new InvalidJwtTokenException("Invalid JWT token");
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
        report.setTargetUserId(targetUser.getId());
        report.setReason(dto.getReason());

        reportRepo.save(report);
    }
}
