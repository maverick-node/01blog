package com.dto;


import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationDTO {
    private String username;
    private Integer id;
    private String type;
    private String message;
    private boolean read;
    private LocalDateTime timestamp;
     private String fromuser;
}
