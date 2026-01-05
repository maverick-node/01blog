package com.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReportedDTO {
    private int id;
    @NotBlank
    private String reporterName;
    @NotBlank
    private String targetUserName;
    @NotBlank
    private String reason;
    private int reportedPostId;
    private LocalDateTime createdAt;
}
