package com.edos.Middleware.dto.ML;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class MLAlertResponse {

    private String message;
    private UUID alertId;
    private String severity;
    private BigDecimal confidence;

    public MLAlertResponse(String message, UUID alertId, String severity, BigDecimal confidence) {
        this.message = message;
        this.alertId = alertId;
        this.severity = severity;
        this.confidence = confidence;
    }

}
