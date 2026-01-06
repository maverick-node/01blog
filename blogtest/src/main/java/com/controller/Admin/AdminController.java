package com.controller.Admin;

import java.util.Collections;
import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dto.MessageResponseDTO;
import com.dto.ReportedDTO;
import com.dto.ResolveReportDTO;
import com.services.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/reports")
    public ResponseEntity<?> getReports(@CookieValue("jwt") String jwt) {
        List<ReportedDTO> reports = adminService.getAllReportsNotResolved(jwt);
        if (reports == null || reports.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return ResponseEntity.ok(reports);
    }

    @GetMapping("/reports-resolved")
    public ResponseEntity<?> getReportsResolved(@CookieValue("jwt") String jwt) {
        List<ReportedDTO> reportsResolved = adminService.getAllReportsResolved(jwt);

        if (reportsResolved.isEmpty()) {

            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(reportsResolved);
    }

    @PostMapping("/resolve-report")
    public MessageResponseDTO resolveReport(@CookieValue("jwt") String jwt, @RequestBody ResolveReportDTO dto) {
        adminService.resolveReport(jwt, dto.getReportId());
        return new MessageResponseDTO("Report resolved successfully");
    }

    @DeleteMapping("/delete-user/{username}")
    public MessageResponseDTO deleteUser(@CookieValue("jwt") String jwt, @PathVariable String username) {
        adminService.deleteUser(jwt, username);
        return new MessageResponseDTO("User deleted successfully");
    }

    @DeleteMapping("/delete-post/{postId}")
    public MessageResponseDTO deletePost(@CookieValue("jwt") String jwt, @PathVariable Integer postId) {
        adminService.deletePost(jwt, postId);
        return new MessageResponseDTO("Post deleted successfully");
    }

    @PostMapping("/ban-user/{username}")
    public MessageResponseDTO banUser(@CookieValue("jwt") String jwt, @PathVariable String username) {
        adminService.banUserOrDeban(jwt, username);
        return new MessageResponseDTO("User banned successfully");
    }
  @PostMapping("/ban-user-report/{username}")
    public MessageResponseDTO banUserReport(@CookieValue("jwt") String jwt, @PathVariable String username) {
        adminService.banUserReport(jwt, username);
        return new MessageResponseDTO("User banned successfully");
    }
    @PostMapping("/reports/{id}/resolve")
    public MessageResponseDTO resolveReport(@CookieValue("jwt") String jwt, @PathVariable Integer id) {
        adminService.resolveReport(jwt, id);
        return new MessageResponseDTO("Report resolved successfully");
    }

    @PostMapping("/reports/hide/{id}")
    public MessageResponseDTO hidePost(@CookieValue("jwt") String jwt, @PathVariable Integer id,
            @RequestBody String reports) {
        adminService.hidePost(jwt, id, reports);
        return new MessageResponseDTO("Report resolved successfully");
    }

    @PostMapping("/reports/unhide/{id}")
    public MessageResponseDTO unhidePost(@CookieValue("jwt") String jwt, @PathVariable Integer id,
            @RequestBody String reports) {
        adminService.unhidePost(jwt, id, reports);
        return new MessageResponseDTO("Report resolved successfully");
    }
}
