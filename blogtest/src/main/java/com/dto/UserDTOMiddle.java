package com.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTOMiddle {
    private String username;
   
    private String mail;

    private String bio;
   
    private int age;
    private String role;
     private boolean isBanned;
}
