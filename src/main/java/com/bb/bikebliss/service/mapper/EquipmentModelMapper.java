package com.bb.bikebliss.service.mapper;

import com.bb.bikebliss.entity.EquipmentModel;
import com.bb.bikebliss.service.dto.EquipmentModelDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {EquipmentMapper.class})
public interface EquipmentModelMapper {
    EquipmentModelMapper INSTANCE = Mappers.getMapper(EquipmentModelMapper.class);

    @Mapping(target = "locationAddress", source = "location.address")
    @Mapping(target = "locationId", source = "location.locationId")
    @Mapping(target = "equipments", ignore = true)
    EquipmentModelDTO equipmentModeltoEquipmentModelDTO(EquipmentModel equipmentModel);

    @Mapping(target = "location", ignore = true)
    EquipmentModel equipmentModelDTOtoEquipmentModel(EquipmentModelDTO equipmentModelDTO);
}