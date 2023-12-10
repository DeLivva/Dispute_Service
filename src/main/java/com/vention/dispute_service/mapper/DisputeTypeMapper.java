package com.vention.dispute_service.mapper;

import com.vention.dispute_service.domain.DisputeTypeEntity;
import com.vention.dispute_service.dto.response.DisputeTypeResponseDTO;
import org.mapstruct.Mapper;

@Mapper
public interface DisputeTypeMapper {
    DisputeTypeResponseDTO convertEntityToDto(DisputeTypeEntity entity);
}
