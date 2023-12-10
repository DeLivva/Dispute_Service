package com.vention.dispute_service.dto.response;

public record DisputeResponseDTO(
        Long id,
        String description,
        String disputeTypeName,
        Long orderId,
        Long userId
) {
}
