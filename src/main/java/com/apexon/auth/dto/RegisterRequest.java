package com.apexon.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

@Data
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Schema(description = "User contact number in 10-digit format")
    @NotBlank(message = "Contact number is mandatory")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Contact number must be a valid 10-digit Indian mobile number"
    )
    private String contactNumber;

    // Getters & setters
}
