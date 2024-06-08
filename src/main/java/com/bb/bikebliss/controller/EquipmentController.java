package com.bb.bikebliss.controller;

import com.bb.bikebliss.service.dto.EquipmentModelDTO;
import com.bb.bikebliss.service.implementation.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipments")
@CrossOrigin(origins = "http://localhost:3000")
public class EquipmentController {
    private final EquipmentService equipmentService;

    @Autowired
    public EquipmentController(EquipmentService equipmentService) {

        this.equipmentService = equipmentService;
    }

    @PostMapping("/addEquipmentModels")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addEquipmentModelWithEquipments(@RequestBody EquipmentModelDTO equipmentModelDTO) {
        try {
            equipmentService.addEquipmentModelWithEquipments(equipmentModelDTO);
            return ResponseEntity.ok("The equipment model and associated equipments have been successfully added.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while adding the equipment model and associated equipments: " + e.getMessage());
        }
    }
    @DeleteMapping("/models/{equipmentModelId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteEquipmentModel(@PathVariable Integer equipmentModelId) {
        try {
            equipmentService.deleteEquipmentModel(equipmentModelId);
            return ResponseEntity.ok("The equipment model and all associated equipments have been successfully deleted.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the equipment model: " + e.getMessage());
        }
    }

    @GetMapping("/equipmentModels")
    public List<EquipmentModelDTO> getAllEquipmentModels() {
        return equipmentService.getAllEquipmentModels();
    }

    @GetMapping("/equipmentModels/{equipmentModelId}")
    public ResponseEntity<EquipmentModelDTO> getEquipmentModelById(@PathVariable Integer equipmentModelId) {
        EquipmentModelDTO equipmentModel = equipmentService.getEquipmentModelById(equipmentModelId);
        if (equipmentModel != null) {
            return ResponseEntity.ok(equipmentModel);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
