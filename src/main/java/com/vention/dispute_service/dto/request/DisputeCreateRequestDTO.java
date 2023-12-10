package com.vention.dispute_service.dto.request;

import jakarta.validation.constraints.NotNull;

public record DisputeCreateRequestDTO(
        @NotNull
        Long orderId,
        @NotNull
        Long disputeTypeId,
        @NotNull
        Long userId,
        @NotNull
        String description
) {
}