package com.bb.bikebliss.service.dto;

import java.util.List;

public record LocationDTO(
        Integer locationId,
        String address,
        List<BikeModelDTO> bikeModels,
        List<EquipmentModelDTO> equipmentModels
) {}
