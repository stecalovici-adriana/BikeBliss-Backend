package com.bb.bikebliss.service.implementation;

import com.bb.bikebliss.entity.*;
import com.bb.bikebliss.repository.*;
import com.bb.bikebliss.service.dto.EquipmentDTO;
import com.bb.bikebliss.service.dto.EquipmentModelDTO;
import com.bb.bikebliss.service.mapper.EquipmentModelMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    private final UserRepository userRepository;
    private final EquipmentRentalRepository equipmentRentalRepository;

    @Autowired
    public EquipmentService(EquipmentModelRepository equipmentModelRepository, EquipmentRepository equipmentRepository,
                            EquipmentModelMapper equipmentModelMapper, LocationRepository locationRepository,
                            UserRepository userRepository, EquipmentRentalRepository equipmentRentalRepository) {
        this.equipmentModelRepository = equipmentModelRepository;
        this.equipmentRepository = equipmentRepository;
        this.equipmentModelMapper = equipmentModelMapper;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.equipmentRentalRepository = equipmentRentalRepository;
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
    @Transactional
    public List<EquipmentModelDTO> getAllEquipmentModels() {
        List<EquipmentModel> equipmentModels = equipmentModelRepository.findAll();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
        String currentUsername = isAuthenticated ? authentication.getName() : null;

        return equipmentModels.stream()
                .map(bikeModel -> getEquipmentModelDTO(bikeModel, currentUsername))
                .collect(Collectors.toList());
    }
    @Transactional
    public EquipmentModelDTO getEquipmentModelById(Integer equipmentModelId) {
        EquipmentModel equipmentModel = equipmentModelRepository.findById(equipmentModelId)
                .orElseThrow(() -> new RuntimeException("Equipment model not found with id: " + equipmentModelId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
        String currentUsername = isAuthenticated ? authentication.getName() : null;

        return getEquipmentModelDTO(equipmentModel, currentUsername);
    }

    @NotNull
    private EquipmentModelDTO getEquipmentModelDTO(EquipmentModel equipmentModel, String currentUsername) {
        List<EquipmentDTO> equipmentDTOs = equipmentRepository.findByEquipmentModelId(equipmentModel.getEquipmentModelId())
                .stream()
                .map(equipment -> new EquipmentDTO(equipment.getEquipmentId(), equipment.getEquipmentStatus()))
                .collect(Collectors.toList());
        BigDecimal pricePerDay = equipmentModel.getPricePerDay();
        BigDecimal discountedPrice = null;
        if (currentUsername != null) {
            User user = userRepository.findByUsername(currentUsername).orElse(null);
            if (user != null) {
                long rentalCount = equipmentRentalRepository.countEquipmentRentalsByUser(user);
                if (rentalCount > 3) {
                    discountedPrice = pricePerDay.multiply(BigDecimal.valueOf(0.85));
                }
            }
        }
        return new EquipmentModelDTO(
                equipmentModel.getEquipmentModelId(),
                equipmentModel.getEquipmentModel(),
                equipmentModel.getEquipmentDescription(),
                pricePerDay,
                discountedPrice,
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
