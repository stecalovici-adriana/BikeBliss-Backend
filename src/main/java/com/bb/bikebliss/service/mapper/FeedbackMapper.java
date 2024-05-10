package com.bb.bikebliss.service.mapper;

import com.bb.bikebliss.entity.Feedback;
import com.bb.bikebliss.service.dto.FeedbackDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {
    FeedbackMapper INSTANCE = Mappers.getMapper(FeedbackMapper.class);

    FeedbackDTO toDto(Feedback feedback);
    Feedback toEntity(FeedbackDTO feedbackDTO);
}
