package com.vention.dispute_service.feign;

import com.vention.dispute_service.config.CustomErrorDecoder;
import com.vention.general.lib.enums.OrderStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(url = "${cloud.core-service.url}", name = "coreService", configuration = CustomErrorDecoder.class)
public interface CoreServiceClient {
    @GetMapping("/api/v1/orders/{id}/status")
    ResponseEntity<OrderStatus> getStatusByOrderId(@PathVariable("id") Long orderId);

    @PutMapping("/api/v1/orders/{id}/status")
    ResponseEntity<Void> changeOrderStatus(@PathVariable("id") Long orderId, @RequestParam("status") OrderStatus status);
}