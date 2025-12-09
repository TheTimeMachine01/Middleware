package com.edos.Middleware.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class MetricsService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // This method will be executed every 5 second
    @Scheduled(fixedRate = 5000)
    public void sendMetricsUpdate() {
        // Here you would gather your metrics data
        Map<String, Object> data = generateMockMetrics(); // Replace with actual metrics gathering logic

        // Send the metrics data to all subscribed clients
        messagingTemplate.convertAndSend("/topic/metrics", data);
    }

    private Map<String, Object> generateMockMetrics() {
        Map<String, Object> data = new HashMap<>();

        // Simulate prediction data
        Map<String, Object> prediction = new HashMap<>();
        prediction.put("is_attack", ThreadLocalRandom.current().nextDouble() > 0.95);
        prediction.put("attack_probability", ThreadLocalRandom.current().nextDouble(0.0, 0.1));
        prediction.put("benign_probability", ThreadLocalRandom.current().nextDouble(0.9, 1.0));
        prediction.put("confidence", ThreadLocalRandom.current().nextDouble(0.9, 1.0));
        prediction.put("model_version", "I-MPaFS-BeastMode-v2.0");

        Map<String, Object> baseModelScores = new HashMap<>();
        baseModelScores.put("pred_rf", ThreadLocalRandom.current().nextDouble(0.0, 0.02));
        prediction.put("base_model_scores", baseModelScores);

        Map<String, Object> explanation = new HashMap<>();
        explanation.put("top_base_model", "pred_rf");
        explanation.put("decision_threshold", 0.02);
        prediction.put("explanation", explanation);

        data.put("predictions", Collections.singletonList(prediction));

        // Simulate statistics data
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("total_flows", ThreadLocalRandom.current().nextInt(1, 100));
        statistics.put("processing_time_ms", ThreadLocalRandom.current().nextDouble(500.0, 2000.0));
        statistics.put("throughput_flows_per_sec", ThreadLocalRandom.current().nextDouble(0.1, 1.0));

        data.put("statistics", statistics);

        return data;
    }
}
