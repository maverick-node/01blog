package com.controller.Admin;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Model.UserStruct;
import com.Repository.PostRepo;
import com.Repository.ReportRepo;
import com.Repository.UserRepo;
import com.services.JwtService;

@RestController
public class Admin {
    // Admin-specific endpoints and logic will go here
    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final PostRepo postRepo;
    private final ReportRepo reportRepo;

    public Admin(JwtService jwtService, UserRepo userRepo, PostRepo postRepo, ReportRepo reportRepo) {
        this.jwtService = jwtService;
        this.userRepo = userRepo;
        this.postRepo = postRepo;
        this.reportRepo = reportRepo;
    }

    // ********************************* Reports *********************************/
    @GetMapping("/get-reports")
    public ResponseEntity<Map<String, Object>> getReports(@CookieValue("jwt") String jwt) {
        String username = jwtService.extractUsername(jwt);
        UserStruct userInfo = userRepo.findByUsername(username);
        if (!userInfo.getRole().equals("ADMIN")) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: You are not an admin!"));
        }
        List<ReportStruct> reports = reportRepo.findAll();
        return ResponseEntity.ok().body(Map.of("reports", reports));
    }

    @PostMapping("/resolve-report")
    public ResponseEntity<?> resolveReport(@RequestBody Map<String, Object> request, HttpServletRequest req) {
        String token = (String) request.get("token");
        Long reportId = Long.valueOf((String) request.get("reportId"));

        if (jwtService.validateToken(token, req)) {
            ReportStruct reportStruct = reportRepo.findById(reportId).orElse(null);
            if (reportStruct == null) {
                return new ResponseEntity<>("Report not found", HttpStatus.NOT_FOUND);
            }
            reportStruct.setResolved(true);
            reportRepo.save(reportStruct);
            return new ResponseEntity<>("Report resolved", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
    }

    // ********************************* Users *********************************/
    @PostMapping("/delete-user/{username}")
    public ResponseEntity<Map<String, String>> deleteUser(@CookieValue("jwt") String jwt,
            @PathVariable String username) {
        String adminUsername = jwtService.extractUsername(jwt);
        UserStruct adminUser = this.userRepo.findByUsername(adminUsername);
        if (!adminUser.getRole().equals("ADMIN")) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: You are not an admin!"));
        }
        if (!this.userRepo.existsByUsername(username)) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: User not found!"));
        }
        userRepo.deleteByUsername(username);
        return ResponseEntity.ok().body(Map.of("message", "User deleted successfully!"));
    }

    // ********************************* Posts *********************************/
    public ResponseEntity<Map<String, String>> deletePost(@CookieValue("jwt") String jwt,
            @PathVariable Integer postId) {
        String adminUsername = jwtService.extractUsername(jwt);
        UserStruct adminUser = this.userRepo.findByUsername(adminUsername);
        if (!adminUser.getRole().equals("ADMIN")) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Error: You are not an admin!"));
        }
        postRepo.deleteById(postId);
        return ResponseEntity.ok().body(Map.of("message", "Post deleted successfully!"));
    }
}
