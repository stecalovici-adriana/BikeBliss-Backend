package com.bb.bikebliss.repository;

import com.bb.bikebliss.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
    @Query("SELECT e FROM Equipment e WHERE e.equipmentModel.equipmentModelId = :equipmentModelId")
    List<Equipment> findByEquipmentModelId(@Param("equipmentModelId") Integer equipmentModelId);
    @Query("SELECT e FROM Equipment e " +
            "WHERE e.equipmentModel.equipmentModelId = :equipmentModelId " +
            "AND e.equipmentStatus = 'AVAILABLE' " +
            "AND NOT EXISTS ( " +
            "   SELECT 1 FROM EquipmentRental r " +
            "   WHERE r.equipment.equipmentId = e.equipmentId " +
            "   AND r.rentalStatus != com.bb.bikebliss.entity.RentalStatus.COMPLETED " +
            "   AND :startDate < r.endDate " +
            "   AND :endDate > r.startDate " +
            ")")
    List<Equipment> findAvailableEquipments(@Param("equipmentModelId") Integer equipmentModelId,
                                  @Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate);
    @Modifying
    @Query("DELETE FROM Equipment e WHERE e.equipmentModel.equipmentModelId = :equipmentModelId")
    void deleteAllByModelId(Integer equipmentModelId);
    @Query("SELECT COUNT(e) FROM Equipment e WHERE e.equipmentModel.equipmentModelId = :equipmentModelId")
    int countByModelId(@Param("equipmentModelId") Integer equipmentModelId);
}
