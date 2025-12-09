package com.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "notifications")
public class NotificationStruct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private UserStruct user; // The user who receives the notification

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private UserStruct fromUser; // The user who triggered the notification

    private String type;
    private String message;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}
