package com.vention.dispute_service.service.impl;

import com.vention.dispute_service.domain.DisputeEntity;
import com.vention.dispute_service.domain.DisputeTypeEntity;
import com.vention.dispute_service.dto.DisputeCreatedNotificationDTO;
import com.vention.dispute_service.dto.OrderStatusDTO;
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
        if (disputeRepository.existsByOrderId(requestDTO.getOrderId())) {
            throw new ActionNotAllowedException("There is already dispute opened on this order: " + requestDTO.getOrderId());
        }
        var disputeEntity = disputeMapper.convertDtoToEntity(requestDTO);
        disputeEntity.setType(disputeType);
        disputeEntity = disputeRepository.save(disputeEntity);
        coreServiceClient.changeOrderStatus(new OrderStatusDTO(requestDTO.getOrderId(), OrderStatus.DISPUTE_OPENED.name()));
        sendNotification(disputeEntity);
        return disputeMapper.convertEntityToDtoWithStatus(disputeEntity, OrderStatus.DISPUTE_OPENED);
    }

    private void sendNotification(DisputeEntity dispute) {
        var order = coreServiceClient.getOrderById(dispute.getOrderId()).getBody();
        if (Objects.isNull(order)) {
            throw new DataNotFoundException("Order not found with id: " + dispute.getOrderId());
        }
        DisputeCreatedNotificationDTO notificationDTO = new DisputeCreatedNotificationDTO();
        notificationDTO.setDisputeId(dispute.getId());
        notificationDTO.setOrderId(dispute.getOrderId());
        notificationDTO.setDescription(dispute.getDescription());
        notificationDTO.setOwnerName(order.getCostumer().getFirstName() + " " + order.getCostumer().getLastName());
        if (Objects.isNull(order.getCourier())) {
            throw new ActionNotAllowedException("Order has not picked up yet, cannot open dispute for this order");
        }
        notificationDTO.setDriverName(order.getCostumer().getFirstName() + " " + order.getCourier().getLastName());
        notificationPublisher.notifyDisputeCreation(notificationDTO);
    }

    @Override
    public void close(Long id) {
        var dispute = disputeRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Dispute not found with id: " + id));
        var status = getStatusByOrderId(dispute.getOrderId());
        if (Objects.equals(status, OrderStatus.DISPUTE_OPENED)) {
            coreServiceClient.changeOrderStatus(new OrderStatusDTO(dispute.getOrderId(), OrderStatus.DISPUTE_CLOSED_BY_CUSTOMER.name()));
        } else {
            throw new ActionNotAllowedException("Disputes under consideration cannot be closed");
        }
    }

    @Override
    public List<DisputeResponseDTO> getByUserId(Long userId) {
        return disputeRepository.findByUserId(userId).stream()
                .map(dispute -> disputeMapper
                        .convertEntityToDtoWithStatus(dispute, getStatusByOrderId(dispute.getOrderId())))
                .collect(Collectors.toList());
    }

    @Override
    public ResponseWithPaginationDTO<DisputeResponseDTO> getAll(PaginationRequestDTO requestDTO) {
        Pageable pageable = PageRequest.of(requestDTO.getPage(), requestDTO.getSize());
        var all = disputeRepository.findAll(pageable);
        return ResponseWithPaginationDTO.<DisputeResponseDTO>builder()
                .currentPage(requestDTO.getPage())
                .pageSize(requestDTO.getSize())
                .totalItems(all.getTotalElements())
                .totalPages(all.getTotalPages())
                .data(all.stream().map(dispute -> disputeMapper
                                .convertEntityToDtoWithStatus(dispute, getStatusByOrderId(dispute.getOrderId())))
                        .collect(Collectors.toList()))
                .build();
    }

    private DisputeTypeEntity getDisputeType(Long disputeTypeId) {
        return disputeTypeRepository.findById(disputeTypeId)
                .orElseThrow(() -> new DataNotFoundException("Dispute type not found with id: " + disputeTypeId));
    }

    private OrderStatus getStatusByOrderId(Long orderId) {
        var order = coreServiceClient.getOrderById(orderId).getBody();
        return OrderStatus.getByName(order.getStatus());
    }
}
