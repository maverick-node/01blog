package com.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateReportDTO {
    @NotBlank(message = "Reason is required")
    @Column(length = 500)
    private String reason;
    private int reportedPostId;
}
