package com.bb.bikebliss.service.implementation;

import com.bb.bikebliss.entity.Location;
import com.bb.bikebliss.repository.LocationRepository;
import com.bb.bikebliss.service.dto.AddressDTO;
import com.bb.bikebliss.service.dto.LocationDTO;
import com.bb.bikebliss.service.mapper.AddressMapper;
import com.bb.bikebliss.service.mapper.LocationMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LocationService {
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final AddressMapper addressMapper;

    @Autowired
    public LocationService(LocationRepository locationRepository, LocationMapper locationMapper, AddressMapper addressMapper) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
        this.addressMapper = addressMapper;
    }
    public void addLocation(LocationDTO locationDTO) {
        Location location = locationMapper.toEntity(locationDTO);

        locationRepository.save(location);
    }
    public List<AddressDTO> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(addressMapper::toDto)
                .collect(Collectors.toList());
    }

}