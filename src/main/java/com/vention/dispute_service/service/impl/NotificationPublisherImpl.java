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
    private final String EXCHANGE_NAME;

    @Value("${rabbit.notification-routing-key}")
    private final String DISPUTE_CREATION_ROUTING_KEY;

    @Override
    public void notifyDisputeCreation(DisputeCreatedNotificationDTO notificationDTO) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, DISPUTE_CREATION_ROUTING_KEY, notificationDTO);
    }
}
