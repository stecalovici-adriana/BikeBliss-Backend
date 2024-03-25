package com.bb.bikebliss.controller;

import com.bb.bikebliss.entity.User;
import com.bb.bikebliss.entity.VerificationToken;
import com.bb.bikebliss.repository.UserRepository;
import com.bb.bikebliss.repository.VerificationTokenRepository;
import com.bb.bikebliss.service.dto.UserDTO;
import com.bb.bikebliss.service.dto.UserRegistrationDTO;
import com.bb.bikebliss.service.implementation.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserService userService, VerificationTokenRepository verificationTokenRepository, UserRepository userRepository) {
        this.userService = userService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        UserDTO newUser = userService.createUser(registrationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        UserDTO user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        UserDTO user = userService.findByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Integer userId, @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(userId, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/change-password")
    public ResponseEntity<Void> changePassword(@PathVariable Integer userId, @RequestBody Map<String, String> passwordDetails) {
        String newPassword = passwordDetails.get("newPassword");
        userService.changePassword(userId, newPassword);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("token") String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken.isEmpty()) {
            return new ResponseEntity<>("Invalid token.", HttpStatus.BAD_REQUEST);
        }

        User user = verificationToken.get().getUser();
        if (user.getVerified()) {
            return new ResponseEntity<>("User already verified.", HttpStatus.BAD_REQUEST);
        }

        LocalDateTime tokenExpiryDate = verificationToken.get().getExpiryDate();
        if (tokenExpiryDate.isBefore(LocalDateTime.now())) {
            return new ResponseEntity<>("Token expired.", HttpStatus.BAD_REQUEST);
        }

        user.setVerified(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken.get());

        return new ResponseEntity<>("User successfully verified.", HttpStatus.OK);
    }
}