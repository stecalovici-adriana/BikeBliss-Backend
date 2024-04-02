package com.bb.bikebliss.repository;

import com.bb.bikebliss.entity.VerificationToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@SuppressWarnings("unused")
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer> {
    @Query("""
select t from VerificationToken t inner join User u on t.user.userId = u.userId
where t.user.userId = :userId and t.loggedOut = false
""")
    List<VerificationToken> findAllTokensByUser(Integer userId);
    Optional<VerificationToken> findByToken(String token);
    @Modifying
    @Transactional
    @Query("DELETE FROM VerificationToken v WHERE v.user.userId" +
            " = :userId")
    void deleteByUserId(Integer userId);
}