package com.edos.Middleware.service;

import com.edos.Middleware.dto.MonitoringData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import com.edos.Middleware.dto.Statistics;
import com.edos.Middleware.dto.Prediction;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MonitoringService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private Statistics statistics;

    @Autowired
    private Prediction prediction;


    private final Random random = new Random();

    /**
     * This method is scheduled to run every 5 seconds.
     * It generates mock monitoring data and sends it to the "/topic/monitoring" destination.
     * Any client subscribed to this topic will receive the data.
     */
    @Scheduled(fixedRate = 5000)
    public void sendMonitoringUpdate() {
        MonitoringData data = createMockMonitoringData();

        // Send the data to the WebSocket topic
        messagingTemplate.convertAndSend("/topic/monitoring", data);
    }

    /**
     * Helper method to generate some random data for demonstration.
     * In a real application, you would get this data from your actual monitoring source.
     */
    private MonitoringData createMockMonitoringData() {
        // This is just an example of creating a mock object.
        // You would replace this with your actual data generation logic.
        MonitoringData data = new MonitoringData();

        // Create mock statistics
        Statistics stats = new Statistics();
        stats.setTotal_flows(100 + random.nextInt(50));
        stats.setAttack_predictions(random.nextInt(5));
        stats.setBenign_predictions(stats.getTotal_flows() - stats.getAttack_predictions());
        stats.setProcessing_time_ms(random.nextDouble() * 2000);
        stats.setThroughput_flows_per_sec(random.nextDouble() * 1);
        stats.setAverage_confidence(0.9 + random.nextDouble() * 0.1);
        data.setStatistics(stats);

        // Create a mock prediction
        Prediction prediction = new Prediction();
        prediction.set_attack(random.nextBoolean());
        prediction.setAttack_probability(random.nextDouble());
        prediction.setBenign_probability(1.0 - prediction.getAttack_probability());
        prediction.setConfidence(random.nextDouble());
        prediction.setModel_version("I-MPaFS-BeastMode-v2.0");

        Map<String, Double> scores = new HashMap<>();
        scores.put("pred_rf", random.nextDouble() * 0.02);
        prediction.setBase_model_scores(scores);

        Map<String, Object> explanation = new HashMap<>();
        explanation.put("top_base_model", "pred_rf");
        prediction.setExplanation(explanation);

        data.setPredictions(Collections.singletonList(prediction));

        return data;
    }
}
