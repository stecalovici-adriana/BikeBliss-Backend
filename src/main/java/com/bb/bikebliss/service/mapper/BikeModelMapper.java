package com.bb.bikebliss.service.mapper;

import com.bb.bikebliss.entity.BikeModel;
import com.bb.bikebliss.service.dto.BikeModelDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {BikeMapper.class})
public interface BikeModelMapper {
    BikeModelMapper INSTANCE = Mappers.getMapper(BikeModelMapper.class);

    @Mapping(target = "locationAddress", source = "location.address")
    @Mapping(target = "locationId", source = "location.locationId")
    @Mapping(target = "bikes", ignore = true)
    BikeModelDTO bikeModelToBikeModelDTO(BikeModel bikeModel);

    @Mapping(target = "location", ignore = true)
    BikeModel bikeModelDTOToBikeModel(BikeModelDTO bikeModelDTO);
}
