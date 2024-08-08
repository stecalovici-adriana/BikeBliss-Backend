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

        String frontEndUrl = "http://localhost:3000";
        String verificationLink = frontEndUrl + "/verify-email?token=" + token;

        // Construirea corpului emailului în format HTML
        String emailBody = "<p>Dear " + firstName + " " + lastName + ",</p>" +
                "<p>Please verify your account by clicking on the following link:</p>" +
                "<a href='" + verificationLink + "'>" + verificationLink + "</a>" +
                "<p>The link is valid for 24 hours.</p>";

        // Trimite emailul de verificare
        emailService.sendHtmlEmail(savedUser.getEmail(), "Confirm your email", emailBody);


        return new AuthenticationResponse(token, "User registration was successful, please check your email to verify your account.");
    }

    public AuthenticationResponse verifyUser(String token) {
        if (token == null || token.isEmpty()) {
            return new AuthenticationResponse(null, "Invalid token", false);
        }

        Optional<VerificationToken> verificationTokenOpt = verificationTokenRepository.findByToken(token);
        if (verificationTokenOpt.isEmpty()) {
            return new AuthenticationResponse(null, "Token not found", false);
        }

        VerificationToken verificationToken = verificationTokenOpt.get();
        User user = verificationToken.getUser();

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return new AuthenticationResponse(null, "Token expired", false);
        }

        if (user.getVerified()) {
            return new AuthenticationResponse(jwtService.generateToken(user), "User already verified", true);
        }

        user.setVerified(true);
        userRepository.save(user);

        String jwt = jwtService.generateToken(user);

        verificationToken.setUsed(true);
        verificationTokenRepository.save(verificationToken);

        return new AuthenticationResponse(jwt, "User successfully verified", true);
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
        String userRole = user.getUserRole().toString();
        revokeAllTokenByUser(user);
        saveUserToken(jwt, user);

        return new AuthenticationResponse(jwt, "User login was successful", userRole);
    }
    public void processForgotPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = UUID.randomUUID().toString();
            VerificationToken verificationToken = new VerificationToken(null, token, user, LocalDateTime.now().plusHours(1));
            verificationTokenRepository.save(verificationToken);

            String resetLink = "http://localhost:3000/reset-password/" + token;
            String emailBody = "<p>Please click the below link to reset your password:</p>"
                    + "<p><a href=\"" + resetLink + "\">" + resetLink + "</a></p>"
                    + "<p>This link will expire in 1 hour.</p>";

            emailService.sendHtmlEmail(user.getEmail(), "Password Reset Request", emailBody);
        }
    }
    public void resetPassword(String token, String newPassword) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Invalid token"));
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired");
        }

        User user = verificationToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        verificationToken.setUsed(true);
        verificationTokenRepository.save(verificationToken);
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