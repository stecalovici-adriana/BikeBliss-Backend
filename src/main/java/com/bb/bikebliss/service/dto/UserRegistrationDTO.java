package com.bb.bikebliss.service.dto;
import com.bb.bikebliss.validation.PasswordMatch;
import jakarta.validation.constraints.*;

@PasswordMatch
public record UserRegistrationDTO(
        @NotBlank(message = "Full name is required")
        @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters long")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        String email,

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters long")
        String username,

        @NotNull(message = "Age is required")
        @Min(value = 18, message = "User must be at least 18 years old")
        @Max(value = 100, message = "User age must be less than or equal to 100")
        Integer age,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters long")
        String password,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword
) {
}