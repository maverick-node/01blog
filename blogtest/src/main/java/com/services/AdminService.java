package com.services;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Exceptions.ReportNotFoundException;
import com.Exceptions.UnauthorizedActionException;
import com.Model.ReportStruct;
import com.Model.UserStruct;
import com.Repository.PostRepo;
import com.Repository.ReportRepo;
import com.Repository.UserRepo;

@Service
public class AdminService {

    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final PostRepo postRepo;
    private final ReportRepo reportRepo;

    public AdminService(JwtService jwtService, UserRepo userRepo, PostRepo postRepo, ReportRepo reportRepo) {
        this.jwtService = jwtService;
        this.userRepo = userRepo;
        this.postRepo = postRepo;
        this.reportRepo = reportRepo;
   
    }

    private void checkAdmin(String jwt) {
        String username = jwtService.extractUsername(jwt);
        UserStruct user = userRepo.findByUsername(username);
        if (user == null || !user.getRole().equals("ADMIN")) {
            throw new UnauthorizedActionException("You are not an admin!");
        }
    }

    public List<ReportStruct> getAllReports(String jwt) {
        checkAdmin(jwt);
        return reportRepo.findAll();
    }

    public void resolveReport(String jwt, int reportId) {
        checkAdmin(jwt);
        ReportStruct report = reportRepo.findById(reportId);
        if (report == null) {
            throw new ReportNotFoundException("Report not found");
        }
        report.setResolved(true);
        reportRepo.save(report);
    }

    public void deleteUser(String jwt, String username) {
        checkAdmin(jwt);
        if (!userRepo.existsByUsername(username)) {
            throw new com.Exceptions.UserNotFoundException("User not found");
        }
        userRepo.deleteByUsername(username);
    }

    public void deletePost(String jwt, Integer postId) {
        checkAdmin(jwt);
        if (!postRepo.existsById(postId)) {
            throw new ReportNotFoundException("Post not found");
        }
        postRepo.deleteById(postId);
    }
}
