package com.bb.bikebliss.service.dto;

import com.bb.bikebliss.entity.RentalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RentalDTO(
        Integer rentalId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalPrice,
        RentalStatus rentalStatus,
        Integer userId,
        Integer bikeId,
        Integer modelId,
        String bikeModel,
        String bikeDescription,
        String bikeImageURL,
        String locationAddress
) {
}