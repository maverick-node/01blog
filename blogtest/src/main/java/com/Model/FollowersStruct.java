package com.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "subscriptions")
public class FollowersStruct {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "subscriber_id", nullable = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private UserStruct subscriber;

    // The user being followed
    @ManyToOne
    @JoinColumn(name = "target_id", nullable = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)

    private UserStruct target;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
