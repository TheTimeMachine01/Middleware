package com.edos.Middleware.dto;

import lombok.Data;

@Data
public class BatchPredictionResponseDto {

    private String message;
    private int totalPredictions;
    private int attackPredictions;
    private int alertsCreated;
    private double attackPercentage;
    private String severity; // CRITICAL/HIGH/MEDIUM/LOW/NONE
    private boolean alertCreated;
    private String alertId;

    public BatchPredictionResponseDto(String message, int totalPredictions, int attackPredictions, int alertsCreated) {
        this.message = message;
        this.totalPredictions = totalPredictions;
        this.attackPredictions = attackPredictions;
        this.alertsCreated = alertsCreated;
    }

    // new constructor used by batch flow
    public BatchPredictionResponseDto(String message, int totalPredictions, int attackPredictions, double attackPercentage,
                                      String severity, boolean alertCreated, String alertId) {
        this.message = message;
        this.totalPredictions = totalPredictions;
        this.attackPredictions = attackPredictions;
        this.attackPercentage = attackPercentage;
        this.severity = severity;
        this.alertCreated = alertCreated;
        this.alertId = alertId;
        this.alertsCreated = alertCreated ? 1 : 0;
    }
}
