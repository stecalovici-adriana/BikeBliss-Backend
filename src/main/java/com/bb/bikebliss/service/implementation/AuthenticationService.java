package com.bb.bikebliss.service.implementation;

import com.bb.bikebliss.entity.User;
import com.bb.bikebliss.entity.UserRole;
import com.bb.bikebliss.entity.VerificationToken;
import com.bb.bikebliss.exception.EmailAlreadyExistsException;
import com.bb.bikebliss.exception.UsernameAlreadyExistsException;
import com.bb.bikebliss.repository.UserRepository;
import com.bb.bikebliss.repository.VerificationTokenRepository;
import com.bb.bikebliss.response.AuthenticationResponse;
import com.bb.bikebliss.service.dto.UserRegistrationDTO;
import com.bb.bikebliss.service.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(UserRepository userRepository,
                       UserMapper userMapper,
                       BCryptPasswordEncoder passwordEncoder,
                       EmailService emailService,
                       VerificationTokenRepository verificationTokenRepository,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(UserRegistrationDTO registrationDTO) {
        if (userRepository.findByEmail(registrationDTO.email()).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already in use");
        }
        if (userRepository.findByUsername(registrationDTO.username()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username is already taken");
        }

        String[] nameParts = registrationDTO.fullName().trim().split("\\s+");
        String lastName = nameParts[nameParts.length - 1];
        String firstName = String.join(" ", Arrays.copyOf(nameParts, nameParts.length - 1));

        User newUser = userMapper.userRegistrationDTOToUser(registrationDTO);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setPassword(passwordEncoder.encode(registrationDTO.password()));
        newUser.setVerified(false);
        newUser.setUserRole(UserRole.USER);
        User savedUser = userRepository.save(newUser);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(savedUser);
        verificationToken.setExpiryDate(LocalDateTime.now().plusDays(1));
        verificationTokenRepository.save(verificationToken);

        String verificationLink = "http://localhost:8080/api/auth/verify?token=" + token;
        emailService.sendEmail(savedUser.getEmail(), "Confirm your email", "Please click on the following link to verify your email: " + verificationLink);

        return new AuthenticationResponse(null, "User registration was successful, please check your email to verify your account.");
    }

    public AuthenticationResponse verifyUser(String token) {
        Optional<VerificationToken> verificationTokenOpt = verificationTokenRepository.findByToken(token);
        if (verificationTokenOpt.isEmpty() || verificationTokenOpt.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            return new AuthenticationResponse(null, "Verification failed", false);
        }

        VerificationToken verificationToken = verificationTokenOpt.get();
        User user = verificationToken.getUser();
        if (user.getVerified()) {
            return new AuthenticationResponse(null, "User already verified", true);
        }

        user.setVerified(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);

        String jwt = jwtService.generateToken(user);
        return new AuthenticationResponse(jwt, "User successfully verified", true);
    }

    public AuthenticationResponse verifyAndAuthenticate(String token) {
        Optional<VerificationToken> verificationTokenOpt = verificationTokenRepository.findByToken(token);
        if (verificationTokenOpt.isEmpty() || verificationTokenOpt.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Invalid or expired token.");
        }

        VerificationToken verificationToken = verificationTokenOpt.get();
        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);

        String jwt = jwtService.generateToken(user);
        return new AuthenticationResponse(jwt, "User successfully verified and authenticated.", true);
    }


    public AuthenticationResponse authenticate(User request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!user.getVerified()) {
            throw new IllegalStateException("Email is not verified");
        }

        String jwt = jwtService.generateToken(user);

        revokeAllTokenByUser(user);
        saveUserToken(jwt, user);

        return new AuthenticationResponse(jwt, "User login was successful");
    }

    private void revokeAllTokenByUser(User user) {
        List<VerificationToken> validTokens = verificationTokenRepository.findAllTokensByUser(user.getUserId());
        if(validTokens.isEmpty()) {
            return;
        }

        validTokens.forEach(t-> {
            t.setLoggedOut(true);
        });

        verificationTokenRepository.saveAll(validTokens);
    }
    private void saveUserToken(String jwt, User user) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(jwt);
        verificationToken.setUser(user);
        verificationToken.setLoggedOut(false);
        verificationToken.setExpiryDate(LocalDateTime.now().plusWeeks(2));
        verificationTokenRepository.save(verificationToken);
    }
}
