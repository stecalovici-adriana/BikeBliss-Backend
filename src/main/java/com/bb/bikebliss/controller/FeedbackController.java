package com.bb.bikebliss.controller;

import com.bb.bikebliss.service.dto.FeedbackDTO;
import com.bb.bikebliss.service.implementation.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "http://localhost:3000")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/submit/{rentalId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FeedbackDTO> submitFeedback(@PathVariable Integer rentalId, @RequestBody FeedbackDTO feedbackDTO) {
        FeedbackDTO createdFeedback = feedbackService.submitFeedback(rentalId, feedbackDTO);
        return ResponseEntity.ok(createdFeedback);
    }
    @GetMapping("/{rentalId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getFeedback(@PathVariable Integer rentalId) {
        try {
            List<FeedbackDTO> feedbacks = feedbackService.getFeedbacksForRental(rentalId);
            if (feedbacks.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(feedbacks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }
    @GetMapping("/average-ratings/{modelId}")
    public Double getAverageRatingByModelId(@PathVariable Integer modelId) {
        Double averageRating = feedbackService.getAverageRatingByModelId(modelId);
        return averageRating != null ? averageRating : 0.0; // Return 0.0 if no ratings found
    }
    @GetMapping("/detailsFeedback/{modelId}")
    public ResponseEntity<List<FeedbackDTO>> getFeedbackByModelId(@PathVariable Integer modelId) {
        List<FeedbackDTO> feedbacks = feedbackService.getFeedbacksByModelId(modelId);
        if (feedbacks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(feedbacks);
    }
}