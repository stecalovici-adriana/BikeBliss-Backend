package com.bb.bikebliss.repository;

import com.bb.bikebliss.entity.EquipmentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EquipmentModelRepository extends JpaRepository<EquipmentModel, Integer> {
    Optional<EquipmentModel> findByEquipmentModelId(Integer equipmentModelId);
}
