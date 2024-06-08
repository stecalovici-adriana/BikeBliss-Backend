package com.bb.bikebliss.service.mapper;

import com.bb.bikebliss.entity.Rental;
import com.bb.bikebliss.service.dto.RentalDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@SuppressWarnings("unused")
@Mapper(componentModel = "spring", uses = {UserMapper.class, BikeMapper.class})
public interface RentalMapper {
    RentalMapper INSTANCE = Mappers.getMapper(RentalMapper.class);

    @Mappings({
            @Mapping(source = "user.email", target = "email"),
            @Mapping(source = "user.username", target = "username"),
            @Mapping(source = "user.userId", target = "userId"),
            @Mapping(source = "bike.bikeId", target = "bikeId"),
            @Mapping(source = "bike.bikeModel.modelId", target = "modelId"),
            @Mapping(source = "bike.bikeModel.bikeModel", target = "bikeModel"),
            @Mapping(source = "bike.bikeModel.bikeDescription", target = "bikeDescription"),
            @Mapping(source = "bike.bikeModel.imageURL", target = "bikeImageURL"),
            @Mapping(source = "bike.bikeModel.location.address", target = "locationAddress")
    })
    RentalDTO rentalToRentalDTO(Rental rental);
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "bike", ignore = true)
    Rental rentalDTOToRental(RentalDTO rentalDTO);
}