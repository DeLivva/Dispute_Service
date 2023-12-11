package com.vention.dispute_service.config;

import com.vention.dispute_service.exception.ActionNotAllowedException;
import com.vention.general.lib.dto.response.GlobalResponseDTO;
import com.vention.general.lib.exceptions.DataAlreadyExistException;
import com.vention.general.lib.exceptions.DataNotFoundException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = FeignException.class)
    public ResponseEntity<String> feignClientExceptionHandler(FeignException e) {
        log.warn(e.getMessage());
        return ResponseEntity.status(e.status()).body(e.getMessage());
    }

    @ExceptionHandler(value = ActionNotAllowedException.class)
    public ResponseEntity<String> actionNotAllowedExceptionHandler(ActionNotAllowedException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = DataNotFoundException.class)
    public ResponseEntity<GlobalResponseDTO> apiExceptionHandler(DataNotFoundException e) {
        log.warn(e.getMessage());
        return getResponse(e.getMessage(), HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(value = {DataAlreadyExistException.class})
    public ResponseEntity<GlobalResponseDTO> apiExceptionHandler(RuntimeException e) {
        log.warn(e.getMessage());
        return getResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    private ResponseEntity<GlobalResponseDTO> getResponse(String message, int status) {
        return ResponseEntity
                .status(status)
                .body(GlobalResponseDTO.builder()
                        .status(status)
                        .message(message)
                        .time(ZonedDateTime.now())
                        .build());
    }
}
