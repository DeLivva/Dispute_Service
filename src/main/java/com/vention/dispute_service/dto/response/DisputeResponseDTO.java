package com.vention.dispute_service.dto.response;

import com.vention.general.lib.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisputeResponseDTO {
    private Long id;
    private String description;
    private String disputeTypeName;
    private Long orderId;
    private Long userId;
    private OrderStatus status;

}
