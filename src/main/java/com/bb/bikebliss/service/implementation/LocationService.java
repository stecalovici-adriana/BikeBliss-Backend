package com.bb.bikebliss.service.implementation;

import com.bb.bikebliss.entity.Location;
import com.bb.bikebliss.repository.LocationRepository;
import com.bb.bikebliss.service.dto.LocationDTO;
import com.bb.bikebliss.service.mapper.LocationMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class LocationService {
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    @Autowired
    public LocationService(LocationRepository locationRepository, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
    }
    public void addLocation(LocationDTO locationDTO) {
        Location location = locationMapper.toEntity(locationDTO);

        locationRepository.save(location);
    }
}