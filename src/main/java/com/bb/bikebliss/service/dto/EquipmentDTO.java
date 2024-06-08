package com.bb.bikebliss.service.dto;

import com.bb.bikebliss.entity.EquipmentStatus;

public record EquipmentDTO(
        Integer equipmentId,
        EquipmentStatus equipmentStatus
) {
}
