package com.edos.Middleware.dto.ML;

import com.edos.Middleware.dto.NetworkFlowInput;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class MLAlertRequest {

    @JsonProperty("resource_id")
    private UUID resourceId;
    @JsonProperty("source_ip")
    private String sourceIp;
    @JsonProperty("target_ip")
    private String targetIp;
    @JsonProperty("flow_data")
    private NetworkFlowInput flowData;
    private MLPrediction prediction;
}
