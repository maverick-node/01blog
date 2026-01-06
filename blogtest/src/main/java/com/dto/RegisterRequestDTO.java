package com.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
@Data
@Builder

public class RegisterRequestDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String mail;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 10, message = "Username must be 4-10 characters")
    @Pattern(
    regexp = "^[a-zA-Z0-9_]+$",
    message = "Username may contain letters, numbers, and underscores only")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 20, message = "Password must be 6-20 characters")
    private String password;

    @Min(value = 13, message = "Age must be at least 13")
    @Max(value = 100, message = "Age must be less than or equal to 100")
    private int age;

    @NotBlank(message = "Bio is required")
    @Size(max = 150, message = "Bio must be min 5 characters and max 150", min = 5)
    private String bio;

}
