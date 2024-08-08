package com.bb.bikebliss.controller;

import com.bb.bikebliss.service.dto.EquipmentRentalDTO;
import com.bb.bikebliss.service.dto.UnavailableDateDTO;
import com.bb.bikebliss.service.implementation.EquipmentRentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/equipmentRentals")
@CrossOrigin(origins = "http://localhost:3000")
public class EquipmentRentalController {
    private final EquipmentRentalService equipmentRentalService;

    @Autowired
    public EquipmentRentalController(EquipmentRentalService equipmentRentalService) {
        this.equipmentRentalService = equipmentRentalService;
    }
    @PostMapping("/createEquipmentRental/{equipmentModelId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createEquipmentRental(@PathVariable Integer equipmentModelId, @RequestBody EquipmentRentalDTO equipmentRentalDTO) {
        try {
            EquipmentRentalDTO createdEquipmentRental = equipmentRentalService.createEquipmentRental(equipmentRentalDTO, equipmentModelId);
            return ResponseEntity.ok(createdEquipmentRental);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } catch (IllegalArgumentException | UsernameNotFoundException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }
    @PostMapping("/approveEquipmentRental/{equipmentRentalId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveEquipmentRental(@PathVariable Integer equipmentRentalId) {
        try {
            equipmentRentalService.approveEquipmentRental(equipmentRentalId);
            return ResponseEntity.ok("Rental approved successfully.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error approving rental: " + ex.getMessage());
        }
    }
    @PostMapping("/rejectEquipmentRental/{equipmentRentalId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectEquipmentRental(@PathVariable Integer equipmentRentalId) {
        try {
            equipmentRentalService.rejectEquipmentRental(equipmentRentalId);
            return ResponseEntity.ok("Rental rejected successfully.");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error rejecting rental: " + ex.getMessage());
        }
    }
    @GetMapping("/unavailable-dates/{equipmentModelId}")
    public ResponseEntity<List<UnavailableDateDTO>> getUnavailableDates(@PathVariable Integer equipmentModelId) {
        try {
            List<UnavailableDateDTO> unavailableDates = equipmentRentalService.getUnavailableDatesForModel(equipmentModelId);
            return ResponseEntity.ok(unavailableDates);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/user-rentals")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EquipmentRentalDTO>> getUserRentals() {
        try {
            List<EquipmentRentalDTO> userRentals = equipmentRentalService.getUserRentals();
            return ResponseEntity.ok(userRentals);
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/active-rentals")
    public ResponseEntity<List<EquipmentRentalDTO>> getActiveEquipmentRentals() {
        try {
            List<EquipmentRentalDTO> activeRentals = equipmentRentalService.getActiveEquipmentRentals();
            return ResponseEntity.ok(activeRentals);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/pending-rentals")
    public ResponseEntity<List<EquipmentRentalDTO>> getPendingEquipmentRentals() {
        try {
            List<EquipmentRentalDTO> pendingRentals = equipmentRentalService.getPendingEquipmentRentals();
            return ResponseEntity.ok(pendingRentals);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/completed-rentals")
    public ResponseEntity<List<EquipmentRentalDTO>> getCompletedEquipmentRentals() {
        try {
            List<EquipmentRentalDTO> completedRentals = equipmentRentalService.getCompletedEquipmentRentals();
            return ResponseEntity.ok(completedRentals);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @DeleteMapping("/cancelEquipmentRental/{equipmentRentalId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> cancelEquipmentRental(@PathVariable Integer equipmentRentalId) {
        try {
            equipmentRentalService.cancelEquipmentRental(equipmentRentalId);
            return ResponseEntity.ok("Rental canceled successfully.");
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
        }
    }
    @PostMapping("/sendEndRentalReminders")
    public ResponseEntity<?> sendEndRentalReminders() {
        equipmentRentalService.sendEndRentalReminder();
        return ResponseEntity.ok("End rental reminders sent successfully.");
    }
    @GetMapping("/all-rentals")
    public ResponseEntity<List<EquipmentRentalDTO>> getAllRentals() {
        List<EquipmentRentalDTO> allRentals = equipmentRentalService.getAllRentals();
        return ResponseEntity.ok(allRentals);
    }
}
