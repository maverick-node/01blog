package com._blog.myblog.controller.Report;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com._blog.myblog.model.ReportStruct;
import com._blog.myblog.model.UserStruct;
import com._blog.myblog.repository.ReportRepository;
import com._blog.myblog.repository.UserRepository;
import com._blog.myblog.services.JwtService;

import java.util.Optional;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public ReportController(ReportRepository reportRepository,
            UserRepository userRepository,
            JwtService jwtService) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/posts/{postId}/report")
    public ResponseEntity<String> reportPost(@PathVariable Integer postId,
            @RequestBody ReportStruct report,
            @RequestHeader("Authorization") String token) {
        String username = jwtService.extractUsername(token.replace("Bearer ", ""));
        UserStruct reporter = userRepository.findByusername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        report.setReporterId(reporter.getId());
        report.setTargetPostId(postId);

        reportRepository.save(report);
        return ResponseEntity.ok("Post reported successfully!");
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingReports() {
        return ResponseEntity.ok(reportRepository.findByResolvedFalse());
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<String> resolveReport(@PathVariable int id) {
        Optional<ReportStruct> optionalReport = reportRepository.findById(id);
        if (optionalReport.isEmpty())
            return ResponseEntity.notFound().build();

        ReportStruct report = optionalReport.get();
        report.setResolved(true);
        reportRepository.save(report);
        return ResponseEntity.ok("Report resolved successfully");
    }
}
