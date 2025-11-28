package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationDTO {
    private Integer id;
    private String type;
    private String message;
    private boolean read;
}
