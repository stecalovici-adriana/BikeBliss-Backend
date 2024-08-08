package com.bb.bikebliss.service.implementation;

import com.bb.bikebliss.entity.*;
import com.bb.bikebliss.repository.*;
import com.bb.bikebliss.service.dto.BikeDTO;
import com.bb.bikebliss.service.dto.BikeModelDTO;
import com.bb.bikebliss.service.mapper.BikeModelMapper;
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
public class BikeService {

    private final BikeModelRepository bikeModelRepository;
    private final BikeRepository bikeRepository;
    private final BikeModelMapper bikeModelMapper;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    @Autowired
    public BikeService(BikeModelRepository bikeModelRepository, BikeRepository bikeRepository,
                       BikeModelMapper bikeModelMapper, LocationRepository locationRepository,
                       UserRepository userRepository, RentalRepository rentalRepository) {
        this.bikeModelRepository = bikeModelRepository;
        this.bikeRepository = bikeRepository;
        this.bikeModelMapper = bikeModelMapper;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    public void addBikeModelWithBikes(BikeModelDTO bikeModelDTO) {
        BikeModel bikeModel = bikeModelMapper.bikeModelDTOToBikeModel(bikeModelDTO);

        if (bikeModelDTO.locationId() != null) {
            Location location = locationRepository.findById(bikeModelDTO.locationId())
                    .orElseThrow(() -> new RuntimeException("Location not found with id: " + bikeModelDTO.locationId()));
            bikeModel.setLocation(location);
        }

        bikeModelRepository.save(bikeModel);

        List<Bike> bikes = bikeModelDTO.bikes().stream()
                .map(bikeDTO -> new Bike(null, BikeStatus.AVAILABLE, bikeModel, new ArrayList<>()))
                .collect(Collectors.toList());

        bikeRepository.saveAll(bikes);
    }

    @Transactional
    public List<BikeModelDTO> getAllBikeModels() {
        List<BikeModel> bikeModels = bikeModelRepository.findAll();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
        String currentUsername = isAuthenticated ? authentication.getName() : null;

        return bikeModels.stream()
                .map(bikeModel -> getBikeModelDTO(bikeModel, currentUsername))
                .collect(Collectors.toList());
    }
    @Transactional
    public BikeModelDTO getBikeModelById(Integer modelId) {
        BikeModel bikeModel = bikeModelRepository.findById(modelId)
                .orElseThrow(() -> new RuntimeException("Bike model not found with id: " + modelId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
        String currentUsername = isAuthenticated ? authentication.getName() : null;

        return getBikeModelDTO(bikeModel, currentUsername);
    }

    @NotNull
    private BikeModelDTO getBikeModelDTO(BikeModel bikeModel, String currentUsername) {
        List<BikeDTO> bikeDTOs = bikeRepository.findByModelId(bikeModel.getModelId())
                .stream()
                .map(bike -> new BikeDTO(bike.getBikeId(), bike.getBikeStatus()))
                .collect(Collectors.toList());
        BigDecimal pricePerDay = bikeModel.getPricePerDay();
        BigDecimal discountedPrice = null;
        if (currentUsername != null) {
            User user = userRepository.findByUsername(currentUsername).orElse(null);
            if (user != null) {
                long rentalCount = rentalRepository.countRentalsByUser(user);
                if (rentalCount > 3) {
                    discountedPrice = pricePerDay.multiply(BigDecimal.valueOf(0.80));
                }
            }
        }
        return new BikeModelDTO(
                bikeModel.getModelId(),
                bikeModel.getBikeModel(),
                pricePerDay,
                discountedPrice,
                bikeModel.getBikeDescription(),
                bikeModel.getImageURL(),
                bikeModel.getLocation() != null ? bikeModel.getLocation().getAddress() : null,
                bikeModel.getLocation() != null ? bikeModel.getLocation().getLocationId() : null,
                bikeDTOs
        );
    }
    public void deleteBikeModel(Integer modelId) {
        BikeModel bikeModel = bikeModelRepository.findById(modelId)
                .orElseThrow(() -> new RuntimeException("Bike model not found with id: " + modelId));

        bikeRepository.deleteAllByModelId(modelId);
        bikeModelRepository.delete(bikeModel);
    }
}