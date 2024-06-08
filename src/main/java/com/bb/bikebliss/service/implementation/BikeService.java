package com.bb.bikebliss.service.implementation;

import com.bb.bikebliss.entity.*;
import com.bb.bikebliss.repository.BikeModelRepository;
import com.bb.bikebliss.repository.BikeRepository;
import com.bb.bikebliss.repository.LocationRepository;
import com.bb.bikebliss.service.dto.BikeDTO;
import com.bb.bikebliss.service.dto.BikeModelDTO;
import com.bb.bikebliss.service.mapper.BikeModelMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    public BikeService(BikeModelRepository bikeModelRepository, BikeRepository bikeRepository,
                       BikeModelMapper bikeModelMapper, LocationRepository locationRepository) {
        this.bikeModelRepository = bikeModelRepository;
        this.bikeRepository = bikeRepository;
        this.bikeModelMapper = bikeModelMapper;
        this.locationRepository = locationRepository;
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

    public List<BikeModelDTO> getAllBikeModels() {
        List<BikeModel> bikeModels = bikeModelRepository.findAll();
        return bikeModels.stream().map(this::getBikeModelDTO).collect(Collectors.toList());
    }
    public BikeModelDTO getBikeModelById(Integer modelId) {
        BikeModel bikeModel = bikeModelRepository.findById(modelId)
                .orElseThrow(() -> new RuntimeException("Bike model not found with id: " + modelId));

        return getBikeModelDTO(bikeModel);
    }

    @NotNull
    private BikeModelDTO getBikeModelDTO(BikeModel bikeModel) {
        List<BikeDTO> bikeDTOs = bikeRepository.findByModelId(bikeModel.getModelId())
                .stream()
                .map(bike -> new BikeDTO(bike.getBikeId(), bike.getBikeStatus()))
                .collect(Collectors.toList());

        return new BikeModelDTO(
                bikeModel.getModelId(),
                bikeModel.getBikeModel(),
                bikeModel.getPricePerDay(),
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