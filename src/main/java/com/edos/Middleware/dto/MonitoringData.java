package com.edos.Middleware.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

// Main DTO for the entire JSON payload
@Data
public class MonitoringData {
    private List<Prediction> predictions;
    private Statistics statistics;
}