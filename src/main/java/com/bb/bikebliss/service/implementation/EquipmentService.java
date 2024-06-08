package com.bb.bikebliss.service.implementation;

import com.bb.bikebliss.entity.*;
import com.bb.bikebliss.repository.*;
import com.bb.bikebliss.service.dto.EquipmentDTO;
import com.bb.bikebliss.service.dto.EquipmentModelDTO;
import com.bb.bikebliss.service.mapper.EquipmentModelMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EquipmentService {
    private final EquipmentModelRepository equipmentModelRepository;
    private final EquipmentRepository equipmentRepository;
    private final EquipmentModelMapper equipmentModelMapper;
    private final LocationRepository locationRepository;

    @Autowired
    public EquipmentService(EquipmentModelRepository equipmentModelRepository, EquipmentRepository equipmentRepository,
                            EquipmentModelMapper equipmentModelMapper, LocationRepository locationRepository) {
        this.equipmentModelRepository = equipmentModelRepository;
        this.equipmentRepository = equipmentRepository;
        this.equipmentModelMapper = equipmentModelMapper;
        this.locationRepository = locationRepository;
    }
    public void addEquipmentModelWithEquipments(EquipmentModelDTO equipmentModelDTO) {
        EquipmentModel equipmentModel = equipmentModelMapper.equipmentModelDTOtoEquipmentModel(equipmentModelDTO);

        if (equipmentModelDTO.locationId() != null) {
            Location location = locationRepository.findById(equipmentModelDTO.locationId())
                    .orElseThrow(() -> new RuntimeException("Location not found with id: " + equipmentModelDTO.locationId()));
            equipmentModel.setLocation(location);
        }

        equipmentModelRepository.save(equipmentModel);

        List<Equipment> equipments = equipmentModelDTO.equipments().stream()
                .map(equipmentDTO -> new Equipment(null, EquipmentStatus.AVAILABLE, equipmentModel, new ArrayList<>()))
                .collect(Collectors.toList());

        equipmentRepository.saveAll(equipments);
    }

    public List<EquipmentModelDTO> getAllEquipmentModels() {
        List<EquipmentModel> equipmentModels = equipmentModelRepository.findAll();
        return equipmentModels.stream().map(this::getEquipmentModelDTO).collect(Collectors.toList());
    }
    public EquipmentModelDTO getEquipmentModelById(Integer equipmentModelId) {
        EquipmentModel equipmentModel = equipmentModelRepository.findById(equipmentModelId)
                .orElseThrow(() -> new RuntimeException("Equipment model not found with id: " + equipmentModelId));

        return getEquipmentModelDTO(equipmentModel);
    }

    @NotNull
    private EquipmentModelDTO getEquipmentModelDTO(EquipmentModel equipmentModel) {
        List<EquipmentDTO> equipmentDTOs = equipmentRepository.findByEquipmentModelId(equipmentModel.getEquipmentModelId())
                .stream()
                .map(equipment -> new EquipmentDTO(equipment.getEquipmentId(), equipment.getEquipmentStatus()))
                .collect(Collectors.toList());

        return new EquipmentModelDTO(
                equipmentModel.getEquipmentModelId(),
                equipmentModel.getEquipmentModel(),
                equipmentModel.getEquipmentDescription(),
                equipmentModel.getPricePerDay(),
                equipmentModel.getImageURL(),
                equipmentModel.getLocation() != null ? equipmentModel.getLocation().getAddress() : null,
                equipmentModel.getLocation() != null ? equipmentModel.getLocation().getLocationId() : null,
                equipmentDTOs
        );
    }
    public void deleteEquipmentModel(Integer equipmentModelId) {
        EquipmentModel equipmentModel = equipmentModelRepository.findById(equipmentModelId)
                .orElseThrow(() -> new RuntimeException("Equipment model not found with id: " + equipmentModelId));

        equipmentRepository.deleteAllByModelId(equipmentModelId);
        equipmentModelRepository.delete(equipmentModel);
    }
}
