package com.bb.bikebliss.service.dto;

import com.bb.bikebliss.entity.UserRole;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserDTO(
        Boolean isVerified,

        Integer userId,

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

        @NotNull(message = "Birth date is required")
        @Past(message = "Birth date must be in the past")
        LocalDate birthDate,

        @NotNull(message = "Account creation timestamp must not be null")
        LocalDateTime accountCreated,

        LocalDateTime lastLogin,

        @NotNull(message = "User role must not be null")
        UserRole userRole
) {}