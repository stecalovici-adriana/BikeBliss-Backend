package com.bb.bikebliss.repository;

import com.bb.bikebliss.entity.Rental;
import com.bb.bikebliss.entity.RentalStatus;
import com.bb.bikebliss.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Integer> {
    @Query("SELECT r FROM Rental r WHERE r.bike.bikeId = :bikeId AND " +
            "(r.startDate <= :endDate AND r.endDate >= :startDate)")
    List<Rental> findOverlappingRentals(@Param("bikeId") Integer bikeId,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);
    @Query("SELECT r FROM Rental r WHERE r.bike.bikeModel.modelId = :modelId")
    List<Rental> findRentalsByModelId(@Param("modelId") Integer modelId);
    List<Rental> findByUser(User user);
    @Query("SELECT COUNT(r) FROM Rental r WHERE r.user = :user")
    long countRentalsByUser(@Param("user") User user);
    List<Rental> findByRentalStatus(RentalStatus status);
    List<Rental> findByRentalStatusAndUser(RentalStatus status, User user);
    List<Rental> findAll();
}