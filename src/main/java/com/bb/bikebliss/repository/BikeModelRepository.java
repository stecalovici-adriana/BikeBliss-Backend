package com.bb.bikebliss.repository;

import com.bb.bikebliss.entity.BikeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BikeModelRepository extends JpaRepository<BikeModel, Integer> {
    Optional<BikeModel> findById(Integer modelId);
}