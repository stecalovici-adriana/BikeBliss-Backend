package com.bb.bikebliss.service.dto;

import com.bb.bikebliss.entity.Equipment;

import java.math.BigDecimal;
import java.util.List;

public record EquipmentModelDTO(
        Integer equipmentModelId,
        String equipmentModel,
        String equipmentDescription,
        BigDecimal pricePerDay,
        String imageURL,
        Integer locationId,
        List<Equipment> equipment
) {}
