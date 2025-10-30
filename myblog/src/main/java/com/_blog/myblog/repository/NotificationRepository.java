package com._blog.myblog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com._blog.myblog.model.NotificationStruct;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationStruct, Integer> {

    List<NotificationStruct> findByUserIdOrderByCreatedAtDesc(Integer userId);

    Optional<NotificationStruct> findById(int id);

    List<NotificationStruct> findByUserIdAndReadFalseOrderByCreatedAtDesc(Integer userId);
}
