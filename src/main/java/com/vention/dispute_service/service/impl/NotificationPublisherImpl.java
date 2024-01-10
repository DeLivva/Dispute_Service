package com.vention.dispute_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vention.dispute_service.dto.DisputeCreatedNotificationDTO;
import com.vention.dispute_service.dto.GeneralDTO;
import com.vention.dispute_service.dto.NotificationDTO;
import com.vention.dispute_service.enums.NotificationType;
import com.vention.dispute_service.service.NotificationPublisher;
import com.vention.general.lib.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationPublisherImpl implements NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${rabbit.exchange}")
    private String exchangeName;

    @Value("${rabbit.notification-routing-key}")
    private String disputeServiceRoutingKey;

    @Override
    public void notifyDisputeCreation(DisputeCreatedNotificationDTO notificationDTO, String courierEmail) {
        String title = "Dispute create on order #" + notificationDTO.getOrderId();
        Map<String, Object> data = new HashMap<>();
        data.put("disputeId", notificationDTO.getDisputeId());
        data.put("orderId", notificationDTO.getOrderId());
        data.put("ownerName", notificationDTO.getOwnerName());
        data.put("driverName", notificationDTO.getDriverName());
        data.put("description", notificationDTO.getDescription());
        data.put("adminEmails", notificationDTO.getAdminEmails());
        NotificationDTO creationNotification = new NotificationDTO(title, courierEmail, data);
        var generalNotificationDTO = new GeneralDTO<>(creationNotification, NotificationType.DISPUTE_CREATION);

        try {
            String json = objectMapper.writeValueAsString(generalNotificationDTO);
            rabbitTemplate.convertAndSend(
                    exchangeName,
                    disputeServiceRoutingKey,
                    json,
                    message -> {
                        MessageProperties properties = message.getMessageProperties();
                        properties.setContentType("application/json");
                        return message;
                    });
        } catch (IOException e) {
            log.error("Error occurred while sending notification: ", e);
            throw new BadRequestException(e.getMessage());
        }
    }
}
