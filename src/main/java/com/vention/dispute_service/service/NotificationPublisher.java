package com.vention.dispute_service.service;

import com.vention.dispute_service.dto.DisputeCreatedNotificationDTO;

public interface NotificationPublisher {
    void notifyDisputeCreation(DisputeCreatedNotificationDTO notificationDTO, String courierEmail);
}
