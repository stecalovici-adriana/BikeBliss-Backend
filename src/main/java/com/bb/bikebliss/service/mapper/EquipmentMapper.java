package com.bb.bikebliss.service.mapper;

import com.bb.bikebliss.entity.Bike;
import com.bb.bikebliss.entity.Equipment;
import com.bb.bikebliss.service.dto.BikeDTO;
import com.bb.bikebliss.service.dto.EquipmentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {
    EquipmentMapper INSTANCE = Mappers.getMapper(EquipmentMapper.class);

    @Mappings({
            @Mapping(source = "equipmentId", target = "equipmentId"),
            @Mapping(source = "equipmentStatus", target = "equipmentStatus")
    })
    EquipmentDTO equipmentToEquipmentDTO(Equipment equipment);

    Bike equipmentDTOToEquipment(EquipmentDTO equipmentDTO);
}
