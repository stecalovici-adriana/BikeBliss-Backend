package com.bb.bikebliss.service.implementation;

import com.bb.bikebliss.entity.*;
import com.bb.bikebliss.repository.EquipmentRentalRepository;
import com.bb.bikebliss.repository.FeedbackEqRepository;
import com.bb.bikebliss.repository.UserRepository;
import com.bb.bikebliss.service.dto.FeedbackDTO;
import com.bb.bikebliss.service.dto.FeedbackEqDTO;
import com.bb.bikebliss.service.mapper.FeedbackEqMapper;
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
public class FeedbackEqService {
    private final FeedbackEqRepository feedbackEqRepository;
    private final EquipmentRentalRepository equipmentRentalRepository;
    private final FeedbackEqMapper feedbackEqMapper;
    private final UserRepository userRepository;

    @Autowired
    public FeedbackEqService(FeedbackEqRepository feedbackEqRepository, EquipmentRentalRepository equipmentRentalRepository,
                           FeedbackEqMapper feedbackEqMapper, UserRepository userRepository) {
        this.feedbackEqRepository = feedbackEqRepository;
        this.equipmentRentalRepository = equipmentRentalRepository;
        this.feedbackEqMapper = feedbackEqMapper;
        this.userRepository = userRepository;
    }

    @Transactional
    public FeedbackEqDTO submitFeedbackEq(Integer equipmentRentalId, FeedbackEqDTO feedbackEqDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        EquipmentRental equipmentRental = equipmentRentalRepository.findById(equipmentRentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        if (!equipmentRental.getUser().equals(user)) {
            throw new IllegalStateException("You are not authorized to provide feedback for this rental.");
        }

        if (equipmentRental.getRentalStatus() != RentalStatus.COMPLETED) {
            throw new IllegalStateException("Feedback can only be provided for completed rentals.");
        }
        if (feedbackEqRepository.existsByEquipmentRental(equipmentRental)) {
            throw new IllegalStateException("Feedback has already been submitted for this rental.");
        }
        Feedback feedback = feedbackEqMapper.toFeedback(feedbackEqDTO);
        feedback.setEquipmentRental(equipmentRental);
        feedback.setUser(user);
        feedback.setFeedbackDate(LocalDateTime.now());

        Feedback savedFeedback = feedbackEqRepository.save(feedback);

        return feedbackEqMapper.toFeedbackEqDTO(savedFeedback);
    }
    public List<FeedbackEqDTO> getFeedbacksForEquipmentRental(Integer equipmentRentalId) {
        EquipmentRental equipmentRental = equipmentRentalRepository.findById(equipmentRentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));
        List<Feedback> feedbacks = feedbackEqRepository.findByEquipmentRental(equipmentRental);
        return feedbacks.stream().map(feedbackEqMapper::toFeedbackEqDTO).collect(Collectors.toList());
    }
    public Double getAverageRatingByEquipmentModelId(Integer equipmentModelId) {
        return feedbackEqRepository.findAverageRatingByEquipmentModelId(equipmentModelId);
    }
    public List<FeedbackEqDTO> getFeedbacksByEquipmentModelId(Integer equipmentModelId) {
        return feedbackEqRepository.findAllFeedbacksByEquipmentModelId(equipmentModelId);
    }
}
