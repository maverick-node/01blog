package com.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Model.NotificationStruct;

public interface NotificationRepo  extends JpaRepository<NotificationStruct, Integer> {
    void markAsRead(Integer userId);
   
}
