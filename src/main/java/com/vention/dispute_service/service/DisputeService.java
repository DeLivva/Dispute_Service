package com.vention.dispute_service.service;

import com.vention.dispute_service.dto.request.DisputeCreateRequestDTO;
import com.vention.dispute_service.dto.response.DisputeResponseDTO;
import com.vention.general.lib.dto.request.PaginationRequestDTO;
import com.vention.general.lib.dto.response.ResponseWithPaginationDTO;

import java.util.List;

public interface DisputeService {
    DisputeResponseDTO create(DisputeCreateRequestDTO requestDTO);

    void close(Long id);

    List<DisputeResponseDTO> getByUserId(Long userId);

    ResponseWithPaginationDTO<DisputeResponseDTO> getAll(PaginationRequestDTO requestDTO);

    DisputeResponseDTO getByOrderId(Long orderId);
}
