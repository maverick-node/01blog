package com.controller.Report;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.Model.ReportStruct;
import com.Model.UserStruct;
import com.Repository.ReportRepo;
import com.Repository.UserRepo;
import com.services.JwtService;

@RestController
public class Report {
    private final ReportRepo reportRepo;
    private final JwtService jwtService;
    private final UserRepo userRepo;

    public Report(ReportRepo reportRepo, JwtService jwtService, UserRepo userRepo) {

        this.reportRepo = reportRepo;

        this.jwtService = jwtService;
        this.userRepo = userRepo;

    }

    @PostMapping("/create-report/{username}")
    public ResponseEntity<Map<String, String>> createReport(@CookieValue("jwt") String jwt,
            @PathVariable String username, @RequestBody ReportStruct reportInfo) {
        String user = jwtService.extractUsername(jwt);
        if (!userRepo.existsByUsername(user)) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Invalid JWT token!"));
        }
        UserStruct userReported = userRepo.findByUsername(username);
        if (!userRepo.existsByUsername(userReported.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: Invalid Username Profile!"));
        }

        UserStruct userReporting = userRepo.findByUsername(user);
        if (userReported.equals(userReporting)) {
            return ResponseEntity.badRequest().body(Map.of("message", "you cant report yourself"));
        }
        ReportStruct repoStruct = new ReportStruct();
        repoStruct.setReason(reportInfo.getReason());
        repoStruct.setReporterId(userReporting.getId());
        repoStruct.setTargetUserId(userReported.getId());
        reportRepo.save(repoStruct);
        return ResponseEntity.ok(Map.of("message", "success"));
    }

}
