package com.bb.bikebliss.repository;

import com.bb.bikebliss.entity.Bike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BikeRepository extends JpaRepository<Bike, Integer> {
    @Query("SELECT b FROM Bike b WHERE b.bikeModel.modelId = :modelId")
    List<Bike> findByModelId(@Param("modelId") Integer modelId);

    @Query("SELECT b FROM Bike b " +
            "WHERE b.bikeModel.modelId = :modelId " +
            "AND b.bikeStatus = 'AVAILABLE' " +
            "AND NOT EXISTS ( " +
            "   SELECT 1 FROM Rental r " +
            "   WHERE r.bike.bikeId = b.bikeId " +
            "   AND r.rentalStatus != com.bb.bikebliss.entity.RentalStatus.COMPLETED " +
            "   AND :startDate < r.endDate " +
            "   AND :endDate > r.startDate " +
            ")")
    List<Bike> findAvailableBikes(@Param("modelId") Integer modelId,
                                  @Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate);
    @Modifying
    @Query("DELETE FROM Bike b WHERE b.bikeModel.modelId = :modelId")
    void deleteAllByModelId(Integer modelId);
    @Query("SELECT COUNT(b) FROM Bike b WHERE b.bikeModel.modelId = :modelId")
    int countByModelId(@Param("modelId") Integer modelId);
}

