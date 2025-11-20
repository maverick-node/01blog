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
    private String targetUsername; 

    private String reason; 
    private boolean resolved = false;
    private Integer reportedPostId; 

    private LocalDateTime createdAt = LocalDateTime.now();
}
