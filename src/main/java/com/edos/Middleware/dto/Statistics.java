package com.edos.Middleware.dto;

import lombok.Data;

@Data
public class Statistics {
    private int total_flows;
    private int attack_predictions;
    private int benign_predictions;
    private double processing_time_ms;
    private double throughput_flows_per_sec;
    private double average_confidence;

}