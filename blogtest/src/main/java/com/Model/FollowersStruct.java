package com.Model;

import jakarta.persistence.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "subscriptions")
public class FollowersStruct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Integer subscriberId;
    private Integer targetId; 
    private LocalDateTime createdAt = LocalDateTime.now();
}
