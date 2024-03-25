package com.bb.bikebliss.repository;


import com.bb.bikebliss.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@SuppressWarnings("unused")
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    List<User> findByFirstName(String firstName);
    List<User> findByLastName(String lastName);
    List<User> findByLastNameAndFirstName(String lastName, String firstName);

    List<User> findByAccountCreatedBetween(LocalDateTime start, LocalDateTime end);
}