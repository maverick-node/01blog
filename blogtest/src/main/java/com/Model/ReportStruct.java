package com.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "reports")
public class ReportStruct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private Integer reporterId;
    private Integer targetUserId; 

    private String reason; 
    private boolean resolved = false; 

    private LocalDateTime createdAt = LocalDateTime.now();
}
