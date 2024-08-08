package com.bb.bikebliss.repository;

import com.bb.bikebliss.entity.EquipmentRental;
import com.bb.bikebliss.entity.Feedback;
import com.bb.bikebliss.service.dto.FeedbackEqDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackEqRepository extends JpaRepository<Feedback, Integer> {
    boolean existsByEquipmentRental(EquipmentRental equipmentRental);
    List<Feedback> findByEquipmentRental(EquipmentRental equipmentRental);
    @Query("SELECT AVG(f.rating) FROM Feedback f " +
            "JOIN f.equipmentRental r " +
            "JOIN r.equipment e " +
            "WHERE e.equipmentModel.equipmentModelId = :equipmentModelId")
    Double findAverageRatingByEquipmentModelId(@Param("equipmentModelId") Integer equipmentModelId);
    @Query("SELECT new com.bb.bikebliss.service.dto.FeedbackEqDTO(f.feedbackId, f.feedbackText, f.rating, f.feedbackDate, u.username, u.userId, r.equipmentRentalId) " +
            "FROM Feedback f JOIN f.equipmentRental r JOIN r.equipment e JOIN f.user u " +
            "WHERE e.equipmentModel.equipmentModelId = :equipmentModelId")
    List<FeedbackEqDTO> findAllFeedbacksByEquipmentModelId(@Param("equipmentModelId") Integer equipmentModelId);
}
