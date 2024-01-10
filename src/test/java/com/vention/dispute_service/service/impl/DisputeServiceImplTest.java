package com.vention.dispute_service.service.impl;

import com.vention.dispute_service.domain.DisputeEntity;
import com.vention.dispute_service.domain.DisputeTypeEntity;
import com.vention.dispute_service.dto.OrderStatusDTO;
import com.vention.dispute_service.dto.request.DisputeCreateRequestDTO;
import com.vention.dispute_service.dto.response.DisputeResponseDTO;
import com.vention.dispute_service.feign.AuthClient;
import com.vention.dispute_service.feign.CoreServiceClient;
import com.vention.dispute_service.mapper.DisputeMapper;
import com.vention.dispute_service.repository.DisputeRepository;
import com.vention.dispute_service.repository.DisputeTypeRepository;
import com.vention.dispute_service.service.NotificationPublisher;
import com.vention.general.lib.dto.request.PaginationRequestDTO;
import com.vention.general.lib.dto.response.OrderResponseDTO;
import com.vention.general.lib.dto.response.ResponseWithPaginationDTO;
import com.vention.general.lib.dto.response.UserResponseDTO;
import com.vention.general.lib.enums.OrderStatus;
import com.vention.general.lib.exceptions.DataNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DisputeServiceImplTest {

    @Mock
    private DisputeRepository disputeRepository;

    @Mock
    private DisputeTypeRepository disputeTypeRepository;

    @Mock
    private DisputeMapper disputeMapper;

    @Mock
    private CoreServiceClient coreServiceClient;

    @Mock
    private NotificationPublisher notificationPublisher;

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private DisputeServiceImpl disputeService;

    @Test
    public void testCreateDispute() {
        // Arrange
        DisputeCreateRequestDTO requestDTO = mock(DisputeCreateRequestDTO.class);
        DisputeTypeEntity disputeType = new DisputeTypeEntity();
        DisputeEntity disputeEntity = new DisputeEntity();
        DisputeResponseDTO expectedResponse = new DisputeResponseDTO(1L, "test", "test", 1L, 1L, OrderStatus.DISPUTE_OPENED); // Assuming DisputeResponseDTO is a valid class

        when(requestDTO.getDisputeTypeId()).thenReturn(1L);
        when(disputeTypeRepository.findById(1L)).thenReturn(Optional.of(disputeType));
        when(disputeMapper.convertDtoToEntity(requestDTO)).thenReturn(disputeEntity);
        when(disputeRepository.save(any(DisputeEntity.class))).thenReturn(disputeEntity);
        when(disputeMapper.convertEntityToDtoWithStatus(disputeEntity, OrderStatus.DISPUTE_OPENED)).thenReturn(expectedResponse);
        when(coreServiceClient.getOrderById(any())).thenReturn(ResponseEntity.ok(getOrderResponse()));
        when(authClient.getAllAdminsEmail()).thenReturn(List.of("testadmin@gmail.com"));
        // Act
        DisputeResponseDTO result = disputeService.create(requestDTO);

        // Assert
        assertEquals(expectedResponse, result);
        verify(disputeRepository).save(disputeEntity);
    }

    @Test
    public void testCreateDisputeIfDisputeTypeNotFound() {
        // Arrange
        DisputeCreateRequestDTO requestDTO = mock(DisputeCreateRequestDTO.class);

        when(requestDTO.getDisputeTypeId()).thenReturn(1L);
        when(disputeTypeRepository.findById(1L)).thenThrow(new DataNotFoundException("dispute type not found with id: " + 1L));

        // Assert
        assertThrows(DataNotFoundException.class, () -> disputeService.create(requestDTO));
    }

    @Test
    public void testCoreServiceClientFails() {
        // Arrange
        DisputeCreateRequestDTO requestDTO = new DisputeCreateRequestDTO(1L, 1L, 1L, "test");
        DisputeTypeEntity disputeType = new DisputeTypeEntity();
        DisputeEntity dispute = new DisputeEntity();
        dispute.setType(disputeType);

        when(disputeTypeRepository.findById(any(Long.class))).thenReturn(Optional.of(disputeType));
        when(disputeMapper.convertDtoToEntity(any(DisputeCreateRequestDTO.class))).thenReturn(dispute);
        when(disputeRepository.save(any(DisputeEntity.class))).thenReturn(dispute);
        doThrow(new RuntimeException())
                .when(coreServiceClient).changeOrderStatus(any(OrderStatusDTO.class));

        // Act and Assert
        assertThrows(RuntimeException.class, () -> disputeService.create(requestDTO));
    }


    @Test
    public void testCloseDispute() {
        // Arrange
        Long disputeId = 1L;
        DisputeEntity disputeEntity = new DisputeEntity(); // Assuming DisputeEntity is a valid class
        disputeEntity.setId(disputeId);
        disputeEntity.setOrderId(123L); // Set a mock order ID

        when(disputeRepository.findById(disputeId)).thenReturn(Optional.of(disputeEntity));
        when(coreServiceClient.getOrderById(123L)).thenReturn(ResponseEntity.ok(getOrderResponse()));

        // Act
        disputeService.close(disputeId);
    }

    @Test
    public void testGetDisputesByUserId() {
        // Arrange
        Long userId = 1L;
        List<DisputeEntity> disputes = List.of(new DisputeEntity(), new DisputeEntity()); // Mock list of disputes
        List<DisputeResponseDTO> expectedResponse = disputes.stream()
                .map(entity -> new DisputeResponseDTO()).toList();

        when(disputeRepository.findByUserId(userId)).thenReturn(disputes);
        when(disputeMapper.convertEntityToDtoWithStatus(any(DisputeEntity.class), any(OrderStatus.class))).thenAnswer(i -> new DisputeResponseDTO());
        when(coreServiceClient.getOrderById(any())).thenReturn(ResponseEntity.ok(getOrderResponse()));

        // Act
        List<DisputeResponseDTO> result = disputeService.getByUserId(userId);

        // Assert
        assertEquals(expectedResponse.size(), result.size());
    }


    @Test
    void getAllDisputes() {
        // Arrange
        PaginationRequestDTO requestDTO = new PaginationRequestDTO(0, 10); // Assuming PaginationRequestDTO is a valid class
        Page<DisputeEntity> page = new PageImpl<>(Arrays.asList(new DisputeEntity(), new DisputeEntity())); // Mock page
        when(disputeRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(disputeMapper.convertEntityToDtoWithStatus(any(DisputeEntity.class), any(OrderStatus.class))).thenAnswer(i -> new DisputeResponseDTO());
        when(coreServiceClient.getOrderById(any())).thenReturn(ResponseEntity.ok(getOrderResponse()));

        // Act
        ResponseWithPaginationDTO<DisputeResponseDTO> result = disputeService.getAll(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getData().size());
    }

    private OrderResponseDTO getOrderResponse() {
        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setCostumer(new UserResponseDTO());
        orderResponseDTO.setCourier(new UserResponseDTO());
        orderResponseDTO.setStatus("DISPUTE_OPENED");
        orderResponseDTO.setId(1L);
        return orderResponseDTO;
    }
}