package com.bb.bikebliss.repository;

import com.bb.bikebliss.entity.Feedback;
import com.bb.bikebliss.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    List<Feedback> findByRental(Rental rental);
}
