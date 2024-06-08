package com.bb.bikebliss.service.dto;

import com.bb.bikebliss.entity.RentalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EquipmentRentalDTO(
        Integer equipmentRentalId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalPrice,
        RentalStatus rentalStatus,
        Integer userId,
        String username,
        String email,
        Integer equipmentId,
        Integer equipmentModelId,
        String equipmentModel,
        String equipmentDescription,
        String equipmentImageURL,
        String locationAddress
) {
}
