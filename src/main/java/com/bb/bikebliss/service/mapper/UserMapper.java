package com.bb.bikebliss.service.mapper;

import com.bb.bikebliss.entity.User;
import com.bb.bikebliss.service.dto.UserDTO;
import com.bb.bikebliss.service.dto.UserRegistrationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@SuppressWarnings("unused")
@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "userId", target = "userId")
    @Mapping(target = "isVerified", source = "verified")
    UserDTO userToUserDTO(User user);

    User userDTOToUser(UserDTO userDTO);

    UserRegistrationDTO userToUserRegistrationDTO(User user);

    User userRegistrationDTOToUser(UserRegistrationDTO userRegistrationDTO);

}