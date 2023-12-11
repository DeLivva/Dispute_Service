package com.vention.dispute_service.service;

import com.vention.dispute_service.dto.response.DisputeTypeResponseDTO;

import java.util.List;

public interface DisputeTypeService {
    List<DisputeTypeResponseDTO> getAll();
}
