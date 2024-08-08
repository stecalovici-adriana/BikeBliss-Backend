package com.bb.bikebliss.controller;

import com.bb.bikebliss.service.dto.EquipmentRentalDTO;
import com.bb.bikebliss.service.dto.RentalDTO;
import com.bb.bikebliss.service.dto.UnavailableDateDTO;
import com.bb.bikebliss.service.implementation.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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

    @PostMapping("/approveRental/{rentalId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveRental(@PathVariable Integer rentalId) {
        try {
            rentalService.approveRental(rentalId);
            return ResponseEntity.ok("Rental approved successfully.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error approving rental: " + ex.getMessage());
        }
    }

    @PostMapping("/rejectRental/{rentalId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectRental(@PathVariable Integer rentalId) {
        try {
            rentalService.rejectRental(rentalId);
            return ResponseEntity.ok("Rental rejected successfully.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error rejecting rental: " + ex.getMessage());
        }
    }

    @GetMapping("/unavailable-dates/{modelId}")
    public ResponseEntity<List<UnavailableDateDTO>> getUnavailableDates(@PathVariable Integer modelId) {
        try {
            List<UnavailableDateDTO> unavailableDates = rentalService.getUnavailableDatesForModel(modelId);
            return ResponseEntity.ok(unavailableDates);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
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
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while canceling the rental.");
        }
    }
    @PostMapping("/sendEndRentalReminders")
    public ResponseEntity<?> sendEndRentalReminders() {
        rentalService.sendEndRentalReminder();
        return ResponseEntity.ok("End rental reminders sent successfully.");
    }
    @GetMapping("/all-bikeRentals")
    public ResponseEntity<List<RentalDTO>> getAllBikeRentals() {
        List<RentalDTO> allBikeRentals = rentalService.getAllBikeRentals();
        return ResponseEntity.ok(allBikeRentals);
    }
}