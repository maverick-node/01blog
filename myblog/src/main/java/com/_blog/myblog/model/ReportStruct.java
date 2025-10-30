package com._blog.myblog.model;

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

    private Integer reporterId; // who is reporting
    private Integer targetUserId; // reported user (optional)
    private Integer targetPostId; // reported post (optional)

    private String reason; // reason for report
    private boolean resolved = false; // admin marks as resolved

    private LocalDateTime createdAt = LocalDateTime.now();
}
