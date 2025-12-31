package com.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class UserProfileDTO {
    
    private String username; 

    @Email(message = "Email must be valid")
    @NotBlank
    private String email;

    @Size(min = 5, max = 100, message = "Bio must be between 5 and 100 characters")
    private String bio;

    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 80, message = "Age must be at most 80")
    private int age;

    private boolean isBanned;  

}
