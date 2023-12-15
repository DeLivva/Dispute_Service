package com.vention.dispute_service.service.impl;

import com.vention.dispute_service.dto.DisputeCreatedNotificationDTO;
import com.vention.dispute_service.service.NotificationPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationPublisherImpl implements NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbit.exchange}")
    private String exchangeName;

    @Value("${rabbit.notification-routing-key}")
    private String disputeServiceRoutingKey;

    @Override
    public void notifyDisputeCreation(DisputeCreatedNotificationDTO notificationDTO) {
        rabbitTemplate.convertAndSend(exchangeName, disputeServiceRoutingKey, notificationDTO);
    }
}
