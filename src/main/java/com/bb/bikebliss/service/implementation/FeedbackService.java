package com.bb.bikebliss.service.implementation;

import com.bb.bikebliss.entity.Feedback;
import com.bb.bikebliss.entity.Rental;
import com.bb.bikebliss.entity.RentalStatus;
import com.bb.bikebliss.entity.User;
import com.bb.bikebliss.repository.FeedbackRepository;
import com.bb.bikebliss.repository.RentalRepository;
import com.bb.bikebliss.repository.UserRepository;
import com.bb.bikebliss.service.dto.FeedbackDTO;
import com.bb.bikebliss.service.mapper.FeedbackMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final RentalRepository rentalRepository;
    private final FeedbackMapper feedbackMapper;
    private final UserRepository userRepository;

    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepository, RentalRepository rentalRepository,
                           FeedbackMapper feedbackMapper, UserRepository userRepository) {
        this.feedbackRepository = feedbackRepository;
        this.rentalRepository = rentalRepository;
        this.feedbackMapper = feedbackMapper;
        this.userRepository = userRepository;
    }

    @Transactional
    public FeedbackDTO submitFeedback(Integer rentalId, FeedbackDTO feedbackDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        if (!rental.getUser().equals(user)) {
            throw new IllegalStateException("You are not authorized to provide feedback for this rental.");
        }

        if (rental.getRentalStatus() != RentalStatus.COMPLETED) {
            throw new IllegalStateException("Feedback can only be provided for completed rentals.");
        }
        if (feedbackRepository.existsByRental(rental)) {
            throw new IllegalStateException("Feedback has already been submitted for this rental.");
        }
        Feedback feedback = feedbackMapper.toFeedback(feedbackDTO);
        feedback.setRental(rental);
        feedback.setUser(user);
        feedback.setFeedbackDate(LocalDateTime.now());

        Feedback savedFeedback = feedbackRepository.save(feedback);

        return feedbackMapper.toFeedbackDTO(savedFeedback);
    }
    public List<FeedbackDTO> getFeedbacksForRental(Integer rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));
        List<Feedback> feedbacks = feedbackRepository.findByRental(rental);
        return feedbacks.stream().map(feedbackMapper::toFeedbackDTO).collect(Collectors.toList());
    }
    public Double getAverageRatingByModelId(Integer modelId) {
        return feedbackRepository.findAverageRatingByModelId(modelId);
    }
    public List<FeedbackDTO> getFeedbacksByModelId(Integer modelId) {
        return feedbackRepository.findAllFeedbacksByModelId(modelId);
    }
}
