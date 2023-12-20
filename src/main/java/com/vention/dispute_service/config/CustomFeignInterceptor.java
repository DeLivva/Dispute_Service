package com.vention.dispute_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomFeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        // Log or modify headers, including the 'Host' header
        System.out.println("Request Headers: " + requestTemplate.headers());
    }
}
