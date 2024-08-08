package com.bb.bikebliss.service.dto;

import java.time.LocalDateTime;

public record FeedbackEqDTO(
        Integer feedbackId,
        String feedbackText,
        Integer rating,
        LocalDateTime feedbackDate,
        String username,
        Integer userId,
        Integer equipmentRentalId
) {
}
