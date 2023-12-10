package com.vention.dispute_service.mapper;

import com.vention.dispute_service.domain.DisputeEntity;
import com.vention.dispute_service.dto.request.DisputeCreateRequestDTO;
import com.vention.dispute_service.dto.response.DisputeResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DisputeMapper {

    DisputeEntity convertDtoToEntity(DisputeCreateRequestDTO createRequestDTO);

    @Mapping(target = "disputeTypeName", source = "type.name")
    DisputeResponseDTO convertEntityToDto(DisputeEntity entity);
}
