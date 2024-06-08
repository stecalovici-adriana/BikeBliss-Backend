package com.bb.bikebliss.controller;

import com.bb.bikebliss.service.dto.BikeModelDTO;
import com.bb.bikebliss.service.implementation.BikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bikes")
@CrossOrigin(origins = "http://localhost:3000")
public class BikeController {

    private final BikeService bikeService;

    @Autowired
    public BikeController(BikeService bikeService) {
        this.bikeService = bikeService;
    }

    @PostMapping("/addModels")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addBikeModelWithBikes(@RequestBody BikeModelDTO bikeModelDTO) {
        try {
            bikeService.addBikeModelWithBikes(bikeModelDTO);
            return ResponseEntity.ok("The bike model and associated bikes have been successfully added.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while adding the bike model and associated bikes: " + e.getMessage());
        }
    }

    @DeleteMapping("/models/{modelId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteBikeModel(@PathVariable Integer modelId) {
        try {
            bikeService.deleteBikeModel(modelId);
            return ResponseEntity.ok("The bike model and all associated bikes have been successfully deleted.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the bike model: " + e.getMessage());
        }
    }

    @GetMapping("/models")
    public List<BikeModelDTO> getAllBikeModels() {
        return bikeService.getAllBikeModels();
    }

    @GetMapping("/models/{modelId}")
    public ResponseEntity<BikeModelDTO> getBikeModelById(@PathVariable Integer modelId) {
        BikeModelDTO bikeModel = bikeService.getBikeModelById(modelId);
        if (bikeModel != null) {
            return ResponseEntity.ok(bikeModel);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}