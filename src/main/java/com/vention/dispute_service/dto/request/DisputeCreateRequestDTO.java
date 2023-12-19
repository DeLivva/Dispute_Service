package com.vention.dispute_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisputeCreateRequestDTO {
    @NotNull
    private Long orderId;

    @NotNull
    private Long disputeTypeId;

    @NotNull
    private Long userId;

    @NotNull
    private String description;
}