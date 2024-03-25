package com.bb.bikebliss.validation;

import com.bb.bikebliss.service.dto.UserRegistrationDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UserRegistrationDTO> {

    @Override
    public boolean isValid(UserRegistrationDTO dto, ConstraintValidatorContext context) {
        return dto.password().equals(dto.confirmPassword());
    }
}