package com.bb.bikebliss.controller;

import com.bb.bikebliss.service.dto.LocationDTO;
import com.bb.bikebliss.service.implementation.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> addLocation(@RequestBody LocationDTO locationDTO) {
        try {
            locationService.addLocation(locationDTO);
            return ResponseEntity.ok("Locația a fost adăugată cu succes.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("A apărut o eroare la adăugarea locației.");
        }
    }
}