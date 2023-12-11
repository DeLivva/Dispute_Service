package com.vention.dispute_service.service.impl;

import com.vention.dispute_service.domain.DisputeEntity;
import com.vention.dispute_service.domain.DisputeTypeEntity;
import com.vention.dispute_service.dto.request.DisputeCreateRequestDTO;
import com.vention.dispute_service.dto.response.DisputeResponseDTO;
import com.vention.dispute_service.feign.CoreServiceClient;
import com.vention.dispute_service.mapper.DisputeMapper;
import com.vention.dispute_service.repository.DisputeRepository;
import com.vention.dispute_service.repository.DisputeTypeRepository;
import com.vention.general.lib.dto.request.PaginationRequestDTO;
import com.vention.general.lib.dto.response.ResponseWithPaginationDTO;
import com.vention.general.lib.enums.OrderStatus;
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

    @InjectMocks
    private DisputeServiceImpl disputeService;

    @Test
    public void testCreateDispute() {
        // Arrange
        DisputeCreateRequestDTO requestDTO = mock(DisputeCreateRequestDTO.class);
        DisputeTypeEntity disputeType = new DisputeTypeEntity(); // Assuming DisputeType is a valid class
        DisputeEntity disputeEntity = new DisputeEntity(); // Assuming DisputeEntity is a valid class
        DisputeResponseDTO expectedResponse = new DisputeResponseDTO(1L, "test", "test", 1L, 1L); // Assuming DisputeResponseDTO is a valid class

        when(requestDTO.disputeTypeId()).thenReturn(1L);
        when(disputeTypeRepository.findById(1L)).thenReturn(Optional.of(disputeType));
        when(disputeMapper.convertDtoToEntity(requestDTO)).thenReturn(disputeEntity);
        when(disputeRepository.save(any(DisputeEntity.class))).thenReturn(disputeEntity);
        when(disputeMapper.convertEntityToDto(disputeEntity)).thenReturn(expectedResponse);

        // Act
        DisputeResponseDTO result = disputeService.create(requestDTO);

        // Assert
        assertEquals(expectedResponse, result);
        verify(disputeRepository).save(disputeEntity);
        verify(coreServiceClient).changeOrderStatus(requestDTO.orderId(), OrderStatus.DISPUTE_OPENED);
    }

    @Test
    public void testCoreServiceClientFails() {
        // Arrange
        DisputeCreateRequestDTO requestDTO = new DisputeCreateRequestDTO(1L, 1L, 1L, "test");
        DisputeTypeEntity disputeType = new DisputeTypeEntity();
        DisputeEntity dispute = new DisputeEntity();
        dispute.setType(disputeType);
        DisputeResponseDTO responseDTO = new DisputeResponseDTO(1L, "test", "Damaged", 1L, 1L);

        when(disputeTypeRepository.findById(any(Long.class))).thenReturn(Optional.of(disputeType));
        when(disputeMapper.convertDtoToEntity(any(DisputeCreateRequestDTO.class))).thenReturn(dispute);
        when(disputeRepository.save(any(DisputeEntity.class))).thenReturn(dispute);
        doThrow(new RuntimeException())
                .when(coreServiceClient).changeOrderStatus(any(Long.class), any(OrderStatus.class));

        // Act and Assert
        assertThrows(RuntimeException.class, () -> disputeService.create(requestDTO));
    }


    @Test
    public void testCloseDispute() {
        // Arrange
        Long disputeId = 1L;
        DisputeEntity disputeEntity = new DisputeEntity(); // Assuming DisputeEntity is a valid class
        disputeEntity.setOrderId(123L); // Set a mock order ID

        when(disputeRepository.findById(disputeId)).thenReturn(Optional.of(disputeEntity));
        when(coreServiceClient.getStatusByOrderId(123L)).thenReturn(ResponseEntity.ok(OrderStatus.CREATED));

        // Act
        disputeService.close(disputeId);

        // Assert
        verify(coreServiceClient).changeOrderStatus(123L, OrderStatus.DISPUTE_CLOSED_BY_CUSTOMER);
    }


    @Test
    public void testGetDisputesByUserId() {
        // Arrange
        Long userId = 1L;
        List<DisputeEntity> disputes = List.of(new DisputeEntity(), new DisputeEntity()); // Mock list of disputes
        List<DisputeResponseDTO> expectedResponse = disputes.stream()
                .map(entity -> new DisputeResponseDTO()).toList();

        when(disputeRepository.findByUserId(userId)).thenReturn(disputes);
        when(disputeMapper.convertEntityToDto(any(DisputeEntity.class))).thenAnswer(i -> new DisputeResponseDTO());

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
        when(disputeMapper.convertEntityToDto(any(DisputeEntity.class))).thenAnswer(i -> new DisputeResponseDTO());

        // Act
        ResponseWithPaginationDTO<DisputeResponseDTO> result = disputeService.getAll(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getData().size());
    }
}