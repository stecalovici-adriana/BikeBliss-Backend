package com.bb.bikebliss.service.dto;

import com.bb.bikebliss.entity.BikeStatus;

public record BikeDTO(
        Integer bikeId,
        BikeStatus bikeStatus
) {
}