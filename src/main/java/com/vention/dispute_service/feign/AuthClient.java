package com.vention.dispute_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "AuthServiceApi", url = "${cloud.auth-service.url}")
public interface AuthClient {

    @GetMapping("/api/v1/users/admin-emails")
    List<String> getAllAdminsEmail();
}
