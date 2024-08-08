package com.bb.bikebliss.repository;

import com.bb.bikebliss.entity.User;
import com.bb.bikebliss.entity.UserRole;
import com.bb.bikebliss.entity.VerificationToken;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class VerificationTokenRepositoryTest extends MySQLContainerGenerator{

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private UserRepository userRepository;

    private VerificationToken verificationToken;

    @BeforeEach
    void setUp() {
        verificationTokenRepository.deleteAll();
        userRepository.deleteAll();
        LocalDate birthDate = LocalDate.of(1985, 1, 1);
        User user = new User(true, null, "test@example.com", "testuser", "passhash", "Test", "User", birthDate, LocalDateTime.now(), null, UserRole.USER);
        userRepository.save(user);

        verificationToken = new VerificationToken(null, "token123", user, LocalDateTime.now().plusDays(1));
        verificationTokenRepository.save(verificationToken);
    }

    @Test
    void whenFindByToken_thenReturnVerificationToken() {
        Optional<VerificationToken> foundToken = verificationTokenRepository.findByToken(verificationToken.getToken());
        assertThat(foundToken.isPresent()).isTrue();
        assertThat(foundToken.get().getToken()).isEqualTo(verificationToken.getToken());
    }

    @Test
    void whenFindByToken_withNonexistentToken_thenNotReturnVerificationToken() {
        Optional<VerificationToken> notFound = verificationTokenRepository.findByToken("nonexistenttoken");
        assertThat(notFound.isPresent()).isFalse();
    }

}