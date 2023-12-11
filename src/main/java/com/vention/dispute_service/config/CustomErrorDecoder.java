package com.vention.dispute_service.config;

import com.vention.general.lib.exceptions.AccessProhibitedException;
import com.vention.general.lib.exceptions.BadRequestException;
import com.vention.general.lib.exceptions.DataNotFoundException;
import com.vention.general.lib.exceptions.InternalServerError;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class CustomErrorDecoder {
    private final ErrorDecoder errorDecoder = new ErrorDecoder.Default();

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            log.warn(methodKey, response);
            int status = response.status();
            switch (status) {
                case 404 -> {
                    return new DataNotFoundException("Data not found not found");
                }
                case 401 -> {
                    return new AccessProhibitedException("Cannot access to the service");
                }
                case 403 -> {
                    return new BadRequestException("Invalid data is provided");
                }
                case 500 -> {
                    return new InternalServerError("Couldn't connect to internal services, try again later");
                }
                default -> {
                    return errorDecoder.decode(methodKey, response);
                }
            }
        };
    }

}