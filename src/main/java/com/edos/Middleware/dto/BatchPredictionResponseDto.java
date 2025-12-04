package com.edos.Middleware.dto;

import lombok.Data;

@Data
public class BatchPredictionResponseDto {

    private String message;
    private int totalPredictions;
    private int attackPredictions;
    private int alertsCreated;

    public BatchPredictionResponseDto(String message, int totalPredictions, int attackPredictions, int alertsCreated) {
        this.message = message;
        this.totalPredictions = totalPredictions;
        this.attackPredictions = attackPredictions;
        this.alertsCreated = alertsCreated;
    }
}
