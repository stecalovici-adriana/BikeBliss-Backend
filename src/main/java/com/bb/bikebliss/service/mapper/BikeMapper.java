package com.bb.bikebliss.service.mapper;


import com.bb.bikebliss.entity.Bike;
import com.bb.bikebliss.service.dto.BikeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BikeMapper {
    BikeMapper INSTANCE = Mappers.getMapper(BikeMapper.class);

    @Mappings({
            @Mapping(source = "bikeId", target = "bikeId"),
            @Mapping(source = "bikeStatus", target = "bikeStatus")
    })
    BikeDTO bikeToBikeDTO(Bike bike);

    Bike bikeDTOToBike(BikeDTO bikeDTO);
}