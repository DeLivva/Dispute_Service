package com.vention.dispute_service.service.impl;

import com.vention.dispute_service.domain.DisputeTypeEntity;
import com.vention.dispute_service.dto.DisputeCreatedNotificationDTO;
import com.vention.dispute_service.dto.request.DisputeCreateRequestDTO;
import com.vention.dispute_service.dto.response.DisputeResponseDTO;
import com.vention.dispute_service.exception.ActionNotAllowedException;
import com.vention.dispute_service.feign.CoreServiceClient;
import com.vention.dispute_service.mapper.DisputeMapper;
import com.vention.dispute_service.repository.DisputeRepository;
import com.vention.dispute_service.repository.DisputeTypeRepository;
import com.vention.dispute_service.service.DisputeService;
import com.vention.dispute_service.service.NotificationPublisher;
import com.vention.general.lib.dto.request.PaginationRequestDTO;
import com.vention.general.lib.dto.response.ResponseWithPaginationDTO;
import com.vention.general.lib.enums.OrderStatus;
import com.vention.general.lib.exceptions.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisputeServiceImpl implements DisputeService {

    private final DisputeRepository disputeRepository;
    private final DisputeTypeRepository disputeTypeRepository;
    private final DisputeMapper disputeMapper;
    private final CoreServiceClient coreServiceClient;

    private final NotificationPublisher notificationPublisher;

    @Override
    @Transactional
    public DisputeResponseDTO create(DisputeCreateRequestDTO requestDTO) {
        DisputeTypeEntity disputeType = getDisputeType(requestDTO.getDisputeTypeId());
        var disputeEntity = disputeMapper.convertDtoToEntity(requestDTO);
        disputeEntity.setType(disputeType);
        disputeEntity = disputeRepository.save(disputeEntity);
        coreServiceClient.changeOrderStatus(requestDTO.getOrderId(), OrderStatus.DISPUTE_OPENED);
        notificationPublisher.notifyDisputeCreation(new DisputeCreatedNotificationDTO(disputeEntity.getOrderId()));
        return disputeMapper.convertEntityToDtoWithStatus(disputeEntity, OrderStatus.DISPUTE_OPENED);
    }

    @Override
    public void close(Long id) {
        var dispute = disputeRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("dispute not found with id: " + id));
        var status = coreServiceClient.getStatusByOrderId(dispute.getOrderId()).getBody();
        if (Objects.equals(status, OrderStatus.DISPUTE_OPENED)) {
            coreServiceClient.changeOrderStatus(dispute.getOrderId(), OrderStatus.DISPUTE_CLOSED_BY_CUSTOMER);
        } else {
            throw new ActionNotAllowedException("disputes under consideration cannot be closed");
        }
    }

    @Override
    public List<DisputeResponseDTO> getByUserId(Long userId) {
        return disputeRepository.findByUserId(userId).stream()
                .map(dispute -> disputeMapper
                        .convertEntityToDtoWithStatus(dispute,
                                coreServiceClient.getStatusByOrderId(dispute.getOrderId()).getBody()))
                .collect(Collectors.toList());
    }

    @Override
    public ResponseWithPaginationDTO<DisputeResponseDTO> getAll(PaginationRequestDTO requestDTO) {
        Pageable pageable = PageRequest.of(requestDTO.page(), requestDTO.size());
        var all = disputeRepository.findAll(pageable);
        return ResponseWithPaginationDTO.<DisputeResponseDTO>builder()
                .currentPage(requestDTO.page())
                .pageSize(requestDTO.size())
                .totalItems(all.getTotalElements())
                .totalPages(all.getTotalPages())
                .data(all.stream().map(dispute -> disputeMapper
                                .convertEntityToDtoWithStatus(dispute,
                                        coreServiceClient.getStatusByOrderId(dispute.getOrderId()).getBody()))
                        .collect(Collectors.toList()))
                .build();
    }

    private DisputeTypeEntity getDisputeType(Long disputeTypeId) {
        return disputeTypeRepository.findById(disputeTypeId)
                .orElseThrow(() -> new DataNotFoundException("dispute type not found with id: " + disputeTypeId));
    }
}
