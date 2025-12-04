package com.edos.Middleware.dto.ML;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

import java.util.Map;

@Data
public class MLPrediction {

    // Getters and setters
    @Getter
    @JsonProperty("is_attack")
    private boolean isAttack;
    @JsonProperty("attack_probability")
    private float attackProbability;
    @JsonProperty("benign_probability")
    private float benignProbability;
    private float confidence;
    @JsonProperty("model_version")
    private String modelVersion;
    @JsonProperty("base_model_scores")
    private Map<String, Float> baseModelScores;
    private Map<String, Object> explanation;

}
