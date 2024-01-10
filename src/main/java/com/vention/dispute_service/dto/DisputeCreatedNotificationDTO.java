package com.vention.dispute_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisputeCreatedNotificationDTO implements Serializable {
    private Long disputeId;
    private Long orderId;
    private String ownerName;
    private String driverName;
    private String description;
    private List<String> adminEmails;
}
