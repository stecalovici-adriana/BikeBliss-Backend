package com.bb.bikebliss.service.dto;

import java.math.BigDecimal;
import java.util.List;

public record BikeModelDTO(
        Integer modelId,
        String bikeModel,
        BigDecimal pricePerDay,
        BigDecimal discountedPrice,
        String bikeDescription,
        String imageURL,
        String locationAddress,
        Integer locationId,
        List<BikeDTO> bikes
) {}