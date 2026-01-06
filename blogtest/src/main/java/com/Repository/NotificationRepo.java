package com.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.Model.LikesStruct;
import com.Model.NotificationStruct;

import jakarta.persistence.LockModeType;

public interface NotificationRepo extends JpaRepository<NotificationStruct, Integer> {
   @Modifying
   @Query("UPDATE NotificationStruct n SET n.read = true WHERE n.id = :id")
   void markAsRead(@Param("id") Integer id);

   List<NotificationStruct> findByUserId(Integer userId);

   @Transactional
   @Modifying
   @Query("UPDATE NotificationStruct n SET n.read = true WHERE n.id = :notificationId")
   void markNotificationAsReadById(@Param("notificationId") Integer notificationId);

   @Transactional
   @Modifying
   @Query("DELETE FROM NotificationStruct n WHERE n.fromUser.id = :userId")
   void deleteByUserId(@Param("userId") Integer userId);

   @Modifying
   @Query("DELETE FROM NotificationStruct n WHERE n.fromUser.id = :userId")
   void deleteByFromUserId(@Param("userId") Integer userId);

   @Modifying
   @Transactional
   @Query("UPDATE NotificationStruct n SET n.read = false WHERE n.id = :id")
   void markAsUnread(@Param("id") Integer id);
      

}
