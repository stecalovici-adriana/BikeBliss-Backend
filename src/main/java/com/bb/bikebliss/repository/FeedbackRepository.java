package com.bb.bikebliss.repository;

import com.bb.bikebliss.entity.Feedback;
import com.bb.bikebliss.entity.Rental;
import com.bb.bikebliss.service.dto.FeedbackDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    boolean existsByRental(Rental rental);
    List<Feedback> findByRental(Rental rental);
    @Query("SELECT AVG(f.rating) FROM Feedback f " +
            "JOIN f.rental r " +
            "JOIN r.bike b " +
            "WHERE b.bikeModel.modelId = :modelId")
    Double findAverageRatingByModelId(@Param("modelId") Integer modelId);
    @Query("SELECT new com.bb.bikebliss.service.dto.FeedbackDTO(f.feedbackId, f.feedbackText, f.rating, f.feedbackDate, u.username, u.userId, r.rentalId) " +
            "FROM Feedback f JOIN f.rental r JOIN r.bike b JOIN f.user u " +
            "WHERE b.bikeModel.modelId = :modelId")
    List<FeedbackDTO> findAllFeedbacksByModelId(@Param("modelId") Integer modelId);
}
