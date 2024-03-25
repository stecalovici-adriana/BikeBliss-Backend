package com.bb.bikebliss.service.dto;

import com.bb.bikebliss.entity.UserRole;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record UserDTO(
        @NotNull(message = "Verified status must not be null")
        Boolean isVerified,

        @Null(message = "ID must be null when creating a new user")
        Integer id,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        String email,

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters long")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters long")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
                message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character")
        String password,

        @NotBlank(message = "First name is required")
        @Size(min = 3, max = 50, message = "First name must be between 3 and 50 characters long")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 3, max = 50, message = "Last name must be between 3 and 50 characters long")
        String lastName,

        @NotNull(message = "Age is required")
        @Min(value = 18, message = "User must be at least 18 years old")
        @Max(value = 100, message = "User age must be less than or equal to 100")
        Integer age,

        @NotNull(message = "Account creation timestamp must not be null")
        LocalDateTime accountCreated,

        LocalDateTime lastLogin,

        @NotNull(message = "User role must not be null")
        UserRole userRole
) {}