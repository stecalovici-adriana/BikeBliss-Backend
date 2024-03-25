package com.bb.bikebliss.repository;

import com.bb.bikebliss.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@SuppressWarnings("unused")
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer> {
    Optional<VerificationToken> findByToken(String token);

}