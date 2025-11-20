package com.controller.Admin;

import java.util.List;
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
    public List<ReportedDTO> getReports(@CookieValue("jwt") String jwt) {
 
        return adminService.getAllReportsNotResolved(jwt);
    }
 @GetMapping("/reports-resolved")
    public List<ReportedDTO> getReportsResolved(@CookieValue("jwt") String jwt) {
 
        return adminService.getAllReportsResolved(jwt);
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
}
