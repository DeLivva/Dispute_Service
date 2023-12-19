package com.vention.dispute_service.service.impl;

import com.vention.dispute_service.dto.response.DisputeTypeResponseDTO;
import com.vention.dispute_service.mapper.DisputeTypeMapper;
import com.vention.dispute_service.repository.DisputeTypeRepository;
import com.vention.dispute_service.service.DisputeTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisputeTypeServiceImpl implements DisputeTypeService {

    private final DisputeTypeRepository repository;
    private final DisputeTypeMapper disputeTypeMapper;

    @Override
    public List<DisputeTypeResponseDTO> getAll() {
        return repository.findAll().stream()
                .map(disputeTypeMapper::convertEntityToDto)
                .collect(Collectors.toList());
    }
}
