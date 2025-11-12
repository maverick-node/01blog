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

    private Integer userId; 
    private Integer fromUserId; 
    
    private String type; 
    private String message; 
    private boolean read = false; 

    private LocalDateTime createdAt = LocalDateTime.now();
}
