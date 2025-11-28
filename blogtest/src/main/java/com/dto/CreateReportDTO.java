package com.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateReportDTO {
    @NotBlank(message = "Reason is required")
    private String reason;
}
