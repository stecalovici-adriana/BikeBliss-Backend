package com.bb.bikebliss.repository;


import com.bb.bikebliss.entity.User;
import com.bb.bikebliss.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        verificationTokenRepository.deleteAll();
        userRepository.deleteAllInBatch();

        LocalDateTime now = LocalDateTime.now();
        user1 = new User(true, null, "user1@example.com", "user1", "passhash1", "User1", "Last1", 30, now.minusDays(2), null, UserRole.USER);
        user2 = new User(true, null, "user2@example.com", "user2", "passhash2", "User2", "Last2", 25, now.minusDays(1), null, UserRole.USER);
        userRepository.saveAll(List.of(user1, user2));
    }

    @Test
    void whenFindByUsername_thenReturnUser() {
        userRepository.findByUsername(user1.getUsername())
                .ifPresent(user -> assertThat(user.getUsername()).isEqualTo(user1.getUsername()));
    }

    @Test
    void whenFindByEmail_thenReturnUser() {
        userRepository.findByEmail(user2.getEmail())
                .ifPresent(user -> assertThat(user.getEmail()).isEqualTo(user2.getEmail()));
    }

    @Test
    void whenExistsByUsername_withExistingUsername_thenReturnTrue() {
        boolean exists = userRepository.existsByUsername(user1.getUsername());
        assertThat(exists).isTrue();
    }

    @Test
    void whenExistsByEmail_withExistingEmail_thenReturnTrue() {
        boolean exists = userRepository.existsByEmail(user2.getEmail());
        assertThat(exists).isTrue();
    }

    @Test
    void whenFindByFirstName_thenReturnListOfUsers() {
        List<User> users = userRepository.findByFirstName(user1.getFirstName());
        assertThat(users).isNotEmpty();
        assertThat(users).extracting(User::getFirstName).contains(user1.getFirstName());
    }

    @Test
    void whenFindByLastName_thenReturnListOfUsers() {
        List<User> users = userRepository.findByLastName(user2.getLastName());
        assertThat(users).isNotEmpty();
        assertThat(users).extracting(User::getLastName).contains(user2.getLastName());
    }

    @Test
    void whenFindByLastNameAndFirstName_thenReturnListOfUsers() {
        List<User> users = userRepository.findByLastNameAndFirstName(user1.getLastName(), user1.getFirstName());
        assertThat(users).isNotEmpty();
        assertThat(users).extracting(User::getFirstName).contains(user1.getFirstName());
        assertThat(users).extracting(User::getLastName).contains(user1.getLastName());
    }

    @Test
    void whenFindByAccountCreatedBetween_thenReturnUsers() {
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now();
        List<User> users = userRepository.findByAccountCreatedBetween(start, end);
        assertThat(users).hasSize(2);
    }

    @Test
    void whenDeleteById_thenUserShouldBeDeleted() {
        verificationTokenRepository.deleteByUserId(user1.getUserId());

        userRepository.deleteById(user1.getUserId());

        assertThat(userRepository.existsById(user1.getUserId())).isFalse();
    }

    @Test
    void whenUpdateUser_thenUserShouldBeUpdated() {
        User savedUser = userRepository.findByUsername(user2.getUsername())
                .orElseThrow(() -> new AssertionError("User not found with username: " + user2.getUsername()));
        savedUser.setLastName("UpdatedLastName");
        userRepository.save(savedUser);

        User updatedUser = userRepository.findById(savedUser.getUserId())
                .orElseThrow(() -> new AssertionError("User not found with id: " + savedUser.getUserId()));
        assertThat(updatedUser.getLastName()).isEqualTo("UpdatedLastName");
    }
}
