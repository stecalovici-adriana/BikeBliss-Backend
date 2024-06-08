package com.bb.bikebliss.repository;

import com.bb.bikebliss.entity.EquipmentRental;
import com.bb.bikebliss.entity.Rental;
import com.bb.bikebliss.entity.RentalStatus;
import com.bb.bikebliss.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRentalRepository extends JpaRepository<EquipmentRental, Integer> {
    @Query("SELECT r FROM EquipmentRental r WHERE r.equipment.equipmentModel.equipmentModelId = :equipmentModelId")
    List<EquipmentRental> findEquipmentRentalsByEquipmentModelId(@Param("equipmentModelId") Integer equipmentModelId);
    List<EquipmentRental> findByUser(User user);
    List<EquipmentRental> findByRentalStatus(RentalStatus status);
    List<EquipmentRental> findByRentalStatusAndUser(RentalStatus status, User user);
}
