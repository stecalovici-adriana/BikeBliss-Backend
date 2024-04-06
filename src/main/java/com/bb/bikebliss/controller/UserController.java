package com.bb.bikebliss.controller;

import com.bb.bikebliss.service.dto.UserDTO;
import com.bb.bikebliss.service.implementation.JwtService;
import com.bb.bikebliss.service.implementation.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public UserController(UserService userService, JwtService jwtService) {

        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/details")
    public ResponseEntity<?> getCurrentUserDetails(HttpServletRequest request) {
        try {
            String token = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
            String username = jwtService.extractUsername(token);

            UserDTO userDto = userService.findByUsername(username);

            if (userDto != null) {
                return ResponseEntity.ok(userDto);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            log.error("Eroare la obtinerea detaliilor utilizatorului: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred");
        }
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

}