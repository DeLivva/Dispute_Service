package com.vention.dispute_service.feign;

import com.vention.dispute_service.config.CustomErrorDecoder;
import com.vention.dispute_service.config.FeignClientConfig;
import com.vention.dispute_service.dto.OrderStatusDTO;
import com.vention.general.lib.dto.response.OrderResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "${cloud.core-service.url}", name = "coreService", configuration = {CustomErrorDecoder.class, FeignClientConfig.CoreFeignClient.class})
public interface CoreServiceClient {
    @GetMapping("/api/v1/orders/{id}")
    ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable("id") Long orderId);

    @PutMapping("/api/v1/orders/status")
    ResponseEntity<Void> changeOrderStatus(@RequestBody OrderStatusDTO status);
}