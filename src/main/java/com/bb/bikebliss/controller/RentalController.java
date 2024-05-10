package com.bb.bikebliss.controller;

import com.bb.bikebliss.service.dto.RentalDTO;
import com.bb.bikebliss.service.dto.UnavailableDateDTO;
import com.bb.bikebliss.service.implementation.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

    @Autowired
    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping("/createRental/{modelId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createRental(@PathVariable Integer modelId, @RequestBody RentalDTO rentalDTO) {
        try {
            RentalDTO createdRental = rentalService.createRental(rentalDTO, modelId);
            return ResponseEntity.ok(createdRental);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } catch (IllegalArgumentException | UsernameNotFoundException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }
    @GetMapping("/unavailable-dates/{modelId}")
    public ResponseEntity<List<UnavailableDateDTO>> getUnavailableDates(@PathVariable Integer modelId) {
        List<UnavailableDateDTO> unavailableDates = rentalService.getUnavailableDatesForModel(modelId);
        return ResponseEntity.ok(unavailableDates);
    }
    @GetMapping("/user-rentals")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RentalDTO>> getUserRentals() {
        try {
            List<RentalDTO> userRentals = rentalService.getUserRentals();
            return ResponseEntity.ok(userRentals);
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/active-rentals")
    public ResponseEntity<List<RentalDTO>> getActiveRentals() {
        try {
            List<RentalDTO> activeRentals = rentalService.getActiveRentals();
            return ResponseEntity.ok(activeRentals);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/pending-rentals")
    public ResponseEntity<List<RentalDTO>> getPendingRentals() {
        try {
            List<RentalDTO> pendingRentals = rentalService.getPendingRentals();
            return ResponseEntity.ok(pendingRentals);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/completed-rentals")
    public ResponseEntity<List<RentalDTO>> getCompletedRentals() {
        try {
            List<RentalDTO> completedRentals = rentalService.getCompletedRentals();
            return ResponseEntity.ok(completedRentals);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @DeleteMapping("/cancelRental/{rentalId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> cancelRental(@PathVariable Integer rentalId) {
        try {
            rentalService.cancelRental(rentalId);
            return ResponseEntity.ok("Rental canceled successfully.");
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }
    @PostMapping("/sendEndRentalReminders")
    public ResponseEntity<?> sendEndRentalReminders() {
        rentalService.sendEndRentalReminder();
        return ResponseEntity.ok("End rental reminders sent successfully.");
    }
}