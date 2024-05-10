package com.bb.bikebliss.service.mapper;

import com.bb.bikebliss.entity.EquipmentModel;
import com.bb.bikebliss.service.dto.EquipmentModelDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EquipmentModelMapper {
    EquipmentModelMapper INSTANCE = Mappers.getMapper(EquipmentModelMapper.class);

    @Mapping(target = "locationId", source = "location.locationId")
    EquipmentModelDTO toDto(EquipmentModel equipmentModel);

    @Mapping(target = "location", ignore = true)
    EquipmentModel toEntity(EquipmentModelDTO equipmentModelDTO);
}