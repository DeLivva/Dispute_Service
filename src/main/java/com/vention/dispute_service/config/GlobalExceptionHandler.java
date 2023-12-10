package com.vention.dispute_service.config;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = FeignException.class)
    public ResponseEntity<String> feignClientExceptionHandler(FeignException e) {
        log.warn(e.getMessage());
        return ResponseEntity.status(e.status()).body(e.getMessage());
    }
}
