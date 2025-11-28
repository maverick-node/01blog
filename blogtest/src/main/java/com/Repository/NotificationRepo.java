package com.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Model.NotificationStruct;

public interface NotificationRepo  extends JpaRepository<NotificationStruct, Integer> {
   @Modifying
    @Query("UPDATE NotificationStruct n SET n.read = true WHERE n.id = :id")
    void markAsRead(@Param("id") Integer id);
   List<NotificationStruct> findByUserId(Integer userId);
   
}
