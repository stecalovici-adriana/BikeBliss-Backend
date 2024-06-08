package com.bb.bikebliss.service.dto;

import java.math.BigDecimal;
import java.util.List;

public record EquipmentModelDTO(
        Integer equipmentModelId,
        String equipmentModel,
        String equipmentDescription,
        BigDecimal pricePerDay,
        String imageURL,
        String locationAddress,
        Integer locationId,
        List<EquipmentDTO> equipments
) {}
