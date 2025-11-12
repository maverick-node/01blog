package com.services;

import org.springframework.stereotype.Service;

import com.Model.NotificationStruct;
import com.Repository.NotificationRepo;


@Service
public class NotificationService {
    private final NotificationRepo notificationRepository;
    public NotificationService(NotificationRepo notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    public void createNotification(Integer userId, Integer fromUserId, String type, String message) {
        NotificationStruct notification = new NotificationStruct();
        notification.setUserId(userId);
        notification.setFromUserId(fromUserId);
        notification.setType(type);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }
}
