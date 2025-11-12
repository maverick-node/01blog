package com._blog.myblog.controller.Admin;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import com._blog.myblog.model.UserStruct;
import com._blog.myblog.model.ReportStruct;
import com._blog.myblog.repository.UserRepository;
import com._blog.myblog.repository.PostRepository;
import com._blog.myblog.repository.ReportRepository;
import com._blog.myblog.services.JwtService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ReportRepository reportRepository;
    private final JwtService jwtService;

    public AdminController(UserRepository userRepository,
            PostRepository postRepository,
            ReportRepository reportRepository,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.reportRepository = reportRepository;
        this.jwtService = jwtService;
    }

    private boolean isAdmin(String token) {
        String username = jwtService.extractUsername(token.replace("Bearer ", ""));
        Optional<UserStruct> optionalUser = userRepository.findByusername(username);
        return optionalUser.isPresent() && "ADMIN".equals(optionalUser.get().getRole());
    }

    // ===================== Users =====================

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String token) {
        if (!isAdmin(token))
            return ResponseEntity.status(403).body("Access denied");
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable Integer id,
            @RequestHeader("Authorization") String token) {
        if (!isAdmin(token))
            return ResponseEntity.status(403).body("Access denied");
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Integer id,
            @RequestHeader("Authorization") String token) {
        if (!isAdmin(token))
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    // ===================== Posts =====================

    @GetMapping("/posts")
    public ResponseEntity<?> getAllPosts(@RequestHeader("Authorization") String token) {
        if (!isAdmin(token))
            return ResponseEntity.status(403).body("Access denied");
        return ResponseEntity.ok(postRepository.findAll());
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<?> getPost(@PathVariable Integer id,
            @RequestHeader("Authorization") String token) {
        if (!isAdmin(token))
            return ResponseEntity.status(403).body("Access denied");
        return postRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Integer id,
            @RequestHeader("Authorization") String token) {
        if (!isAdmin(token))
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        postRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Post deleted successfully"));
    }

    // ===================== Reports =====================

    @GetMapping("/reports")
    public ResponseEntity<Map<String, Object>> getAllReports(@RequestHeader("Authorization") String token) {
        if (!isAdmin(token))
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        return ResponseEntity.ok(Map.of("message", reportRepository.findByResolvedFalse()));
    }

    @PostMapping("/reports/{id}/resolve")
    @Transactional
    public ResponseEntity<Map<String, Object>> resolveReport(@PathVariable int id,
            @RequestHeader("Authorization") String token) {
        try {
            if (!isAdmin(token))
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));

            Optional<ReportStruct> optionalReport = reportRepository.findById(id);
            if (optionalReport.isEmpty())
                return ResponseEntity.notFound().build();

            ReportStruct report = optionalReport.get();
            report.setResolved(true);
            reportRepository.save(report);

            // return updated unresolved reports so frontend can refresh its list
            return ResponseEntity.ok(Map.of("message", reportRepository.findByResolvedFalse()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to resolve report", "details", e.getMessage()));
        }
    }
}
