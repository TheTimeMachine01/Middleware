package com.edos.Middleware.dto.ML;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Prediction {

    @JsonProperty("is_attack")
    private boolean isAttack;

    @JsonProperty("attack_type")
    private String attackType;

    private double confidence;
    private String details;
}
