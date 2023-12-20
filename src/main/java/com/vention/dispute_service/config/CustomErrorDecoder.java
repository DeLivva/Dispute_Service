package com.vention.dispute_service.config;

import com.google.gson.JsonParser;
import com.vention.general.lib.exceptions.AccessProhibitedException;
import com.vention.general.lib.exceptions.BadRequestException;
import com.vention.general.lib.exceptions.DataNotFoundException;
import com.vention.general.lib.exceptions.InternalServerError;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@Configuration
public class CustomErrorDecoder {
    private final ErrorDecoder errorDecoder = new ErrorDecoder.Default();

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            log.warn(methodKey, response);
            int status = response.status();
            try {
                String message = null;
                if (response.body() != null) {
                    String responseBody = Util.toString(response.body().asReader());
                    message = extractErrorMessage(responseBody);
                }


                switch (status) {
                    case 404 -> {
                        message = message != null ? message : "Data not found not found";
                        return new DataNotFoundException(message);
                    }
                    case 401 -> {
                        message = message != null ? message : "Cannot access to the service";
                        return new AccessProhibitedException(message);
                    }
                    case 403 -> {
                        message = message != null ? message : "Invalid data is provided";
                        return new BadRequestException(message);
                    }
                    case 500 -> {
                        message = message != null ? message : "Couldn't connect to internal services, try again later";
                        return new InternalServerError(message);
                    }
                    default -> {
                        return errorDecoder.decode(methodKey, response);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private String extractErrorMessage(String responseBody) {
        try {
            return JsonParser.parseString(responseBody).getAsJsonObject().get("message").getAsString();
        } catch (RuntimeException e) {
            return responseBody;
        }
    }
}