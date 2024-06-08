package com.bb.bikebliss.service.mapper;

import com.bb.bikebliss.entity.EquipmentRental;
import com.bb.bikebliss.service.dto.EquipmentRentalDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@SuppressWarnings("unused")
@Mapper(componentModel = "spring", uses = {UserMapper.class, EquipmentMapper.class})
public interface EquipmentRentalMapper {
    EquipmentRentalMapper INSTANCE = Mappers.getMapper(EquipmentRentalMapper.class);
    @Mappings({
            @Mapping(source = "user.email", target = "email"),
            @Mapping(source = "user.username", target = "username"),
            @Mapping(source = "user.userId", target = "userId"),
            @Mapping(source = "equipment.equipmentId", target = "equipmentId"),
            @Mapping(source = "equipment.equipmentModel.equipmentModelId", target = "equipmentModelId"),
            @Mapping(source = "equipment.equipmentModel.equipmentModel", target = "equipmentModel"),
            @Mapping(source = "equipment.equipmentModel.equipmentDescription", target = "equipmentDescription"),
            @Mapping(source = "equipment.equipmentModel.imageURL", target = "equipmentImageURL"),
            @Mapping(source = "equipment.equipmentModel.location.address", target = "locationAddress")
    })
    EquipmentRentalDTO equipmentRentalToEquipmentRentalDTO(EquipmentRental equipmentRental);
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "equipment", ignore = true)
    EquipmentRental equipmentRentalDTOToEquipmentRental(EquipmentRentalDTO equipmentRentalDTO);
}
