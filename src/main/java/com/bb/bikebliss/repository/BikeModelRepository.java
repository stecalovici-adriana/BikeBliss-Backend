package com.bb.bikebliss.repository;

import com.bb.bikebliss.entity.BikeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BikeModelRepository extends JpaRepository<BikeModel, Integer> {
    Optional<BikeModel> findById(Integer modelId);
}