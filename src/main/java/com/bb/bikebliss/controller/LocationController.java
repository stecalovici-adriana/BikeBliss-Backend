package com.bb.bikebliss.controller;

import com.bb.bikebliss.service.dto.AddressDTO;
import com.bb.bikebliss.service.dto.LocationDTO;
import com.bb.bikebliss.service.implementation.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@CrossOrigin(origins = "http://localhost:3000")
public class LocationController {
    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping("/addLocation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addLocation(@RequestBody LocationDTO locationDTO) {
        try {
            locationService.addLocation(locationDTO);
            return ResponseEntity.ok("The location has been successfully added.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while adding the location.");
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<AddressDTO>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }
}