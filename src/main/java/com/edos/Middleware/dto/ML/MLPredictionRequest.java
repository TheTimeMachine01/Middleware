package com.edos.Middleware.dto.ML;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class MLPredictionRequest {

    @JsonProperty("message_id")
    private String messageId;

    private String timestamp;

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("resource_id")
    private UUID resourceId;

    private Flow flow;
    private Prediction prediction;
    private String source;
}
