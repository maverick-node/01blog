package com.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Exceptions.InvalidJwtTokenException;
import com.Exceptions.UserNotFoundException;
import com.Model.NotificationStruct;
import com.Repository.NotificationRepo;
import com.Repository.UserRepo;

@Service
public class NotificationService {

    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final NotificationRepo notificationRepo;

    public NotificationService(JwtService jwtService, NotificationRepo notificationRepo, UserRepo userRepo) {
        this.jwtService = jwtService;
        this.notificationRepo = notificationRepo;
        this.userRepo = userRepo;
    }

    public List<NotificationStruct> getNotifications(String jwt) {
        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty()) {
            throw new InvalidJwtTokenException("Invalid JWT token");
        }

        var user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return notificationRepo.findByUserId(user.getId());
    }

    public void markAsRead(String jwt, int notifid) {
        String username = jwtService.extractUsername(jwt);
        if (username == null || username.isEmpty()) {
            throw new InvalidJwtTokenException("Invalid JWT token");
        }

        var user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        var notification = notificationRepo.findById(notifid)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        if (notification.isRead()) {
            notificationRepo.markAsUnread(notifid);
        } else {
            notificationRepo.markNotificationAsReadById(notifid);
        }

    }
}
