package com.vention.dispute_service.dto;

import com.vention.dispute_service.enums.NotificationType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GeneralDTO<T> {

    private T body;

    private NotificationType type;

    public GeneralDTO(T body, NotificationType type) {
        this.body = body;
        this.type = type;
    }
}
