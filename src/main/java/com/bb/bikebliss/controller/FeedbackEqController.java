package com.bb.bikebliss.controller;

import com.bb.bikebliss.service.dto.FeedbackDTO;
import com.bb.bikebliss.service.dto.FeedbackEqDTO;
import com.bb.bikebliss.service.implementation.FeedbackEqService;
import com.bb.bikebliss.service.implementation.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbackEq")
@CrossOrigin(origins = "http://localhost:3000")
public class FeedbackEqController {
    private final FeedbackEqService feedbackEqService;

    @Autowired
    public FeedbackEqController(FeedbackEqService feedbackEqService) {
        this.feedbackEqService = feedbackEqService;
    }

    @PostMapping("/submit/{equipmentRentalId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FeedbackEqDTO> submitFeedbackEq(@PathVariable Integer equipmentRentalId, @RequestBody FeedbackEqDTO feedbackEqDTO) {
        FeedbackEqDTO createdFeedback = feedbackEqService.submitFeedbackEq(equipmentRentalId, feedbackEqDTO);
        return ResponseEntity.ok(createdFeedback);
    }
    @GetMapping("/{equipmentRentalId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getFeedback(@PathVariable Integer equipmentRentalId) {
        try {
            List<FeedbackEqDTO> feedbacks = feedbackEqService.getFeedbacksForEquipmentRental(equipmentRentalId);
            if (feedbacks.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(feedbacks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }
    @GetMapping("/average-ratings/{equipmentModelId}")
    public Double getAverageRatingByEquipmentModelId(@PathVariable Integer equipmentModelId) {
        Double averageRating = feedbackEqService.getAverageRatingByEquipmentModelId(equipmentModelId);
        return averageRating != null ? averageRating : 0.0; // Return 0.0 if no ratings found
    }
    @GetMapping("/detailsFeedback/{equipmentModelId}")
    public ResponseEntity<List<FeedbackEqDTO>> getFeedbackByEquipmentModelId(@PathVariable Integer equipmentModelId) {
        List<FeedbackEqDTO> feedbacks = feedbackEqService.getFeedbacksByEquipmentModelId(equipmentModelId);
        if (feedbacks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(feedbacks);
    }
}
