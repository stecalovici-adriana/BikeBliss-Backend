package com.bb.bikebliss.service.mapper;

import com.bb.bikebliss.entity.Location;
import com.bb.bikebliss.service.dto.AddressDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressDTO toDto(Location location);

    Location toEntity(AddressDTO addressDTO);
}
