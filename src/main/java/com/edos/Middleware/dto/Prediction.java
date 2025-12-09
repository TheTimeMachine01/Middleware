package com.edos.Middleware.dto;

import lombok.Data;

import java.util.Map;

@Data
public class Prediction {
    private boolean is_attack;
    private double attack_probability;
    private double benign_probability;
    private double confidence;
    private String model_version;
    private Map<String, Double> base_model_scores;
    private Map<String, Object> explanation;

}
