package com.bb.bikebliss.service.mapper;

import com.bb.bikebliss.entity.Feedback;
import com.bb.bikebliss.service.dto.FeedbackDTO;
import com.bb.bikebliss.service.dto.FeedbackEqDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FeedbackEqMapper {
    FeedbackEqMapper INSTANCE = Mappers.getMapper(FeedbackEqMapper.class);
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "equipmentRental.equipmentRentalId", target = "equipmentRentalId")
    FeedbackEqDTO toFeedbackEqDTO(Feedback feedback);
    Feedback toFeedback(FeedbackEqDTO feedbackEqDTO);
}
