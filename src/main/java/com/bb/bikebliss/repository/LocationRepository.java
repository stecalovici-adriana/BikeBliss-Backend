package com.bb.bikebliss.repository;

import com.bb.bikebliss.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    @Override
    Optional<Location> findById(Integer integer);
}