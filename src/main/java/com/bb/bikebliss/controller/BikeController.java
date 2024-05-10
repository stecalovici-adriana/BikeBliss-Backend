package com.bb.bikebliss.controller;

import com.bb.bikebliss.service.dto.BikeModelDTO;
import com.bb.bikebliss.service.implementation.BikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> addBikeModelWithBikes(@RequestBody BikeModelDTO bikeModelDTO) {
        try {
            bikeService.addBikeModelWithBikes(bikeModelDTO);
            return ResponseEntity.ok("Modelul de bicicletă și bicicletele asociate au fost adăugate cu succes.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("A apărut o eroare la adăugarea modelului de bicicletă și a bicicletelor asociate.");
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