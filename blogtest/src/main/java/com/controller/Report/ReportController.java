package com.controller.Report;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Model.ReportStruct;
import com.Model.UserStruct;
import com.Repository.ReportRepo;
import com.Repository.UserRepo;
import com.dto.CreateReportDTO;
import com.services.JwtService;
import com.services.ReportService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/create/{username}")
    public ResponseEntity<Map<String, String>> createReport(@Valid @PathVariable String username,
            @CookieValue("jwt") String jwt,
            @RequestBody @Valid CreateReportDTO dto) {

        reportService.createReport(username, jwt, dto);

        return ResponseEntity.ok(Map.of("message", "Report created successfully"));
    }

    @PostMapping("/report-post")
    public ResponseEntity<Map<String, String>> reportPost(@Valid @CookieValue("jwt") String jwt,
            @RequestBody @Valid CreateReportDTO dto) {

        if (dto.getReason().trim().isEmpty() || dto.getReason().trim().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Reason cannot be empty or blank"));
        }
        if (dto.getReportedPostId() == 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "Reported Post ID cannot be null"));
        }

        if (dto.getReason().length() > 500) {
            return ResponseEntity.badRequest().body(Map.of("message", "Details cannot be more than 500 characters"));
        }
        reportService.reportPost(dto.getReportedPostId(), jwt, dto);
        return ResponseEntity.ok(Map.of("message", "Post reported successfully"));
    }
}