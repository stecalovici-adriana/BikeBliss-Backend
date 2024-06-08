package com.bb.bikebliss.service.mapper;

import com.bb.bikebliss.entity.Feedback;
import com.bb.bikebliss.service.dto.FeedbackDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {
    FeedbackMapper INSTANCE = Mappers.getMapper(FeedbackMapper.class);
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "rental.rentalId", target = "rentalId")
    FeedbackDTO toFeedbackDTO(Feedback feedback);
    Feedback toFeedback(FeedbackDTO feedbackDTO);
}
