package com.bb.bikebliss.controller;

import com.bb.bikebliss.entity.User;
import com.bb.bikebliss.entity.VerificationToken;
import com.bb.bikebliss.exception.EmailAlreadyExistsException;
import com.bb.bikebliss.exception.UsernameAlreadyExistsException;
import com.bb.bikebliss.repository.UserRepository;
import com.bb.bikebliss.repository.VerificationTokenRepository;
import com.bb.bikebliss.response.AuthenticationResponse;
import com.bb.bikebliss.service.dto.UserRegistrationDTO;
import com.bb.bikebliss.service.implementation.AuthenticationService;
import com.bb.bikebliss.service.implementation.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authService;
    private final VerificationTokenRepository verificationTokenRepository;

    @Autowired
    public AuthController(AuthenticationService authService,
                          VerificationTokenRepository verificationTokenRepository) {
        this.authService = authService;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody UserRegistrationDTO registrationDTO
    ) {
        return ResponseEntity.ok(authService.register(registrationDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody User request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/verify")
    public ResponseEntity<AuthenticationResponse> verifyUser(@RequestParam("token") String token) {
        AuthenticationResponse response = authService.verifyUser(token);
        if (!response.isVerified()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthenticationResponse(null, response.getMessage(), false));
        }

        return ResponseEntity.ok(new AuthenticationResponse(response.getToken(), "User successfully verified", true));
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        authService.processForgotPassword(email);
        return ResponseEntity.ok("If an account with that email exists, we have sent a link to reset your password.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password reset successful.");
    }
}