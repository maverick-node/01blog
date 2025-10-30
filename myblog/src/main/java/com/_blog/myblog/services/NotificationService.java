package com._blog.myblog.services;

import org.springframework.stereotype.Service;
import com._blog.myblog.model.NotificationStruct;
import com._blog.myblog.repository.NotificationRepository;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    public NotificationService(NotificationRepository notificationRepository) {
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
