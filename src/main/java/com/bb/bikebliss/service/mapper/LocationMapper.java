package com.bb.bikebliss.service.mapper;

import com.bb.bikebliss.entity.Location;
import com.bb.bikebliss.service.dto.LocationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {BikeModelMapper.class, EquipmentModelMapper.class})
public interface LocationMapper {

    LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);

    @Mapping(target = "bikeModels", source = "bikeModels")
    @Mapping(target = "equipmentModels", source = "equipmentModels")
    LocationDTO toDto(Location location);

    @Mapping(target = "bikeModels", source = "bikeModels")
    @Mapping(target = "equipmentModels", source = "equipmentModels")
    Location toEntity(LocationDTO locationDTO);
}