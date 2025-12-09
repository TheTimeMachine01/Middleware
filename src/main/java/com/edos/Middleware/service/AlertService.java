package com.edos.Middleware.service;

import com.edos.Middleware.dto.Alert;
import com.edos.Middleware.dto.BatchPredictionResponseDto;
import com.edos.Middleware.dto.ML.MLAlertRequest;
import com.edos.Middleware.dto.ML.MLAlertResponse;
import com.edos.Middleware.dto.ML.MLPredictionRequest;
import com.edos.Middleware.dto.SecurityAlertDto;
import com.edos.Middleware.entity.AlertStatus;
import com.edos.Middleware.entity.SecurityAlert;
import com.edos.Middleware.repository.SecurityAlertRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlertService {

    @Autowired
    private SecurityAlertRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // used to push live events to websocket clients

    @Autowired
    private GeoIpService geoIpService;

    public Alert getLatestAlert() throws Exception {
        ClassPathResource resource = new ClassPathResource("data.json");
        try (InputStream in = resource.getInputStream()) {
            return objectMapper.readValue(in, Alert.class);
        }
    }

    public Optional<MLAlertResponse> createAlertFromMLPrediction(MLAlertRequest request, Long userId) {
        if (request.getPrediction() == null || !request.getPrediction().isAttack()) {
            return Optional.empty();
        }

        SecurityAlert alert = new SecurityAlert();
        alert.setId(UUID.randomUUID());
        alert.setUserId(userId);
        alert.setResourceId(request.getResourceId());
        alert.setSourceIp(request.getSourceIp());
        alert.setTargetIp(request.getTargetIp());

        // Map prediction data
        var prediction = request.getPrediction();
        alert.setConfidenceScore(BigDecimal.valueOf(prediction.getConfidence() * 100.0));
        alert.setDetectionMethod("ML_PREDICTION_" + prediction.getModelVersion());

        // Set default/derived values
        String severity = calculateSeverity(prediction.getConfidence(), prediction.getAttackProbability());
        alert.setSeverity(severity);
        alert.setTitle("ML-Detected Malicious Activity");
        alert.setDescription("Potential threat detected by ML model " + prediction.getModelVersion() + " with confidence " + alert.getConfidenceScore() + "%.");
        alert.setCategory("Network Anomaly");
        alert.setStatus(AlertStatus.NEW);
        alert.setRead(false);
        alert.setDetectedAt(Instant.now());

        try {
            alert.setRawData(objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            // Consider logging this error
            alert.setRawData("{\"error\": \"Failed to serialize raw data\"}");
        }

        SecurityAlert savedAlert = repository.save(alert);

        // send a live websocket event for this single alert
        try {
            Map<String, Object> event = new LinkedHashMap<>();
            event.put("type", "alert");
            event.put("id", savedAlert.getId().toString());
            event.put("severity", savedAlert.getSeverity());
            event.put("title", savedAlert.getTitle());
            event.put("description", savedAlert.getDescription());
            event.put("source_ip", savedAlert.getSourceIp());
            event.put("target_ip", savedAlert.getTargetIp());
            event.put("confidence", savedAlert.getConfidenceScore());
            event.put("detectedAt", savedAlert.getDetectedAt());
            // include raw prediction if available
            event.put("raw", savedAlert.getRawData());
            messagingTemplate.convertAndSend("/topic/network-monitor/events", event);
        } catch (Exception ignored) {
            // Do not break processing on websocket failures
        }

        MLAlertResponse response = new MLAlertResponse(
                "Alert created from ML prediction",
                savedAlert.getId(),
                savedAlert.getSeverity(),
                savedAlert.getConfidenceScore()
        );
        return Optional.of(response);
    }

    private String calculateSeverity(float confidence, float attackProbability) {
        float score = confidence * attackProbability;
        if (score > 0.9) return "CRITICAL";
        if (score > 0.7) return "HIGH";
        if (score > 0.5) return "MEDIUM";
        return "LOW";
    }

    // Existing methods (findAlertsForUser, buildSpec, toDto)
    public Page<SecurityAlertDto> findAlertsForUser(Long userId, String level, Boolean read, Pageable pageable) {
        Specification<SecurityAlert> spec = buildSpec(userId, level, read);
        Page<SecurityAlert> page = repository.findAll(spec, pageable);
        return page.map(this::toDto);
    }

    private Specification<SecurityAlert> buildSpec(Long userId, String level, Boolean read) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("userId"), userId));
            if (level != null && !level.isBlank()) {
                predicates.add(cb.equal(root.get("severity"), level));
            }
            if (read != null) {
                predicates.add(cb.equal(root.get("read"), read));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private SecurityAlertDto toDto(SecurityAlert e) {
        SecurityAlertDto dto = new SecurityAlertDto();
        dto.setId(e.getId());
        dto.setLevel(e.getSeverity());
        dto.setMessage(e.getDescription());
        dto.setSource(e.getSourceIp());
        dto.setTimestamp(e.getDetectedAt());
        dto.setTitle(e.getTitle());
        dto.setCategory(e.getCategory());
        dto.setConfidence(e.getConfidenceScore());
        dto.setTarget_ip(e.getTargetIp());
        dto.setTarget_port(e.getTargetPort());
        dto.setDetection_method(e.getDetectionMethod());
        dto.setRead(e.getRead());
        return dto;
    }

    private String calculateSeverity(double confidence) {
        if (confidence > 0.9) return "CRITICAL";
        if (confidence > 0.7) return "HIGH";
        if (confidence > 0.5) return "MEDIUM";
        return "LOW";
    }

    @Transactional
    public BatchPredictionResponseDto createAlertsFromBatchPredictions(List<MLPredictionRequest> requests, Long userId) {
        int totalPredictions = requests.size();

        List<SecurityAlert> alertsToCreate = requests.stream()
                .filter(req -> req.getPrediction() != null && req.getPrediction().isAttack())
                .map(req -> {
                    SecurityAlert alert = new SecurityAlert();
                    alert.setId(UUID.randomUUID());
                    alert.setUserId(userId); // Corrected to use Long userId
                    alert.setResourceId(req.getResourceId());
                    alert.setTitle("ML-Detected " + req.getPrediction().getAttackType());
                    alert.setDescription(req.getPrediction().getDetails());
                    alert.setSeverity(calculateSeverity(req.getPrediction().getConfidence()));
                    alert.setSourceIp(req.getFlow().getSrcIp());
                    alert.setTargetIp(req.getFlow().getDstIp());
                    alert.setTargetPort(req.getFlow().getDstPort());
                    alert.setConfidenceScore(BigDecimal.valueOf(req.getPrediction().getConfidence()));
                    alert.setDetectionMethod(req.getSource());
                    alert.setStatus(AlertStatus.NEW);
                    alert.setCategory("network"); // Default category
                    try {
                        alert.setRawData(objectMapper.writeValueAsString(req));
                    } catch (JsonProcessingException e) {
                        // Handle exception or set raw data to a default error message
                        alert.setRawData("{\"error\": \"Failed to serialize request\"}");
                    }
                    return alert;
                })
                .collect(Collectors.toList());

        int attackPredictions = alertsToCreate.size();
        double attackPercentage = totalPredictions == 0 ? 0.0 : (attackPredictions * 100.0) / totalPredictions;

        // Determine aggregated severity from attackPercentage
        String aggregatedSeverity;
        if (attackPercentage >= 75.0) {
            aggregatedSeverity = "CRITICAL";
        } else if (attackPercentage >= 50.0) {
            aggregatedSeverity = "HIGH";
        } else if (attackPercentage >= 25.0) {
            aggregatedSeverity = "MEDIUM";
        } else if (attackPercentage > 0.0) {
            aggregatedSeverity = "LOW";
        } else {
            aggregatedSeverity = "NONE";
        }

        // Create individual alerts for each attack prediction (existing behavior)
        if (!alertsToCreate.isEmpty()) {
            repository.saveAll(alertsToCreate);
        }

        // send per-attack events to websocket subscribers (if any)
        try {
            for (MLPredictionRequest req : requests) {
                if (req.getPrediction() == null || !req.getPrediction().isAttack()) continue;
                Map<String, Object> evt = new LinkedHashMap<>();
                evt.put("type", "event");
                evt.put("resourceId", req.getResourceId() != null ? req.getResourceId().toString() : null);
                evt.put("srcIp", req.getFlow() != null ? req.getFlow().getSrcIp() : null);
                evt.put("dstIp", req.getFlow() != null ? req.getFlow().getDstIp() : null);
                evt.put("srcPort", req.getFlow() != null ? req.getFlow().getSrcPort() : null);
                evt.put("dstPort", req.getFlow() != null ? req.getFlow().getDstPort() : null);
                evt.put("attackType", req.getPrediction().getAttackType());
                evt.put("confidence", req.getPrediction().getConfidence());
                evt.put("details", req.getPrediction().getDetails());
                evt.put("timestamp", Instant.now().toString());
                // geo information (if request contains it under a source object) - best-effort
                try {
                    // if there's a Source/Geo attached (not in MLPredictionRequest currently), include it
                    // else frontend can resolve IP to lat/long using a geo service.
                    // We intentionally don't call external services here.
                } catch (Exception ignored) {}

                messagingTemplate.convertAndSend("/topic/network-monitor/events", evt);
            }
        } catch (Exception ignored) {}

        // Optionally create a single aggregated alert for the batch when severity is at least MEDIUM
        boolean aggregatedAlertCreated = false;
        UUID aggregatedAlertId = null;
        if (!aggregatedSeverity.equals("NONE") && !aggregatedSeverity.equals("LOW")) {
            SecurityAlert batchAlert = new SecurityAlert();
            batchAlert.setId(UUID.randomUUID());
            aggregatedAlertId = batchAlert.getId();
            batchAlert.setUserId(userId);
            batchAlert.setTitle("Aggregated ML Batch Alert: " + aggregatedSeverity);
            batchAlert.setDescription(String.format("Batch processed: %d total, %d attacks (%.2f%%) -> %s",
                    totalPredictions, attackPredictions, attackPercentage, aggregatedSeverity));
            batchAlert.setSeverity(aggregatedSeverity);
            batchAlert.setCategory("network-batch");
            batchAlert.setStatus(AlertStatus.NEW);
            batchAlert.setRead(false);
            batchAlert.setDetectedAt(Instant.now());
            batchAlert.setDetectionMethod("ML_BATCH");
            batchAlert.setConfidenceScore(BigDecimal.valueOf(attackPercentage));
            try {
                batchAlert.setRawData(objectMapper.writeValueAsString(Map.of(
                        "total", totalPredictions,
                        "attackCount", attackPredictions,
                        "attackPercentage", attackPercentage
                )));
            } catch (JsonProcessingException e) {
                batchAlert.setRawData("{\"error\": \"Failed to serialize batch metadata\"}");
            }
            repository.save(batchAlert);
            aggregatedAlertCreated = true;

            // send aggregated summary over websocket as well
            try {
                Map<String, Object> summary = new LinkedHashMap<>();
                summary.put("type", "summary");
                summary.put("totalPredictions", totalPredictions);
                summary.put("attackPredictions", attackPredictions);
                summary.put("attackPercentage", attackPercentage);
                summary.put("severity", aggregatedSeverity);
                summary.put("alertId", aggregatedAlertId.toString());
                summary.put("timestamp", Instant.now().toString());
                messagingTemplate.convertAndSend("/topic/network-monitor/summary", summary);
            } catch (Exception ignored) {}
        }

        String message = String.format("Processed %d predictions, created %d alerts", totalPredictions, attackPredictions);
        return new BatchPredictionResponseDto(message, totalPredictions, attackPredictions, attackPercentage,
                aggregatedSeverity, aggregatedAlertCreated, aggregatedAlertId != null ? aggregatedAlertId.toString() : null);
    }

    public SecurityAlert createManualAlert(Map<String, Object> payload, Long userId) {
        SecurityAlert alert = new SecurityAlert();

        alert.setId(UUID.randomUUID());
        alert.setUserId(userId);
        alert.setDetectedAt(Instant.now());
        alert.setStatus(AlertStatus.NEW);
        alert.setRead(false);

        alert.setTitle((String) payload.getOrDefault("title", "Manual Test Alert"));

        String description = payload.containsKey("message") ? (String) payload.get("message") : (String) payload.get("description");
        alert.setDescription(description != null ? description : "This is a test alert from a manual trigger.");

        alert.setSourceIp((String) payload.get("source"));
        alert.setSeverity(((String) payload.getOrDefault("severity", "low")).toUpperCase());
        alert.setCategory((String) payload.getOrDefault("category", "manual"));
        alert.setDetectionMethod("manual");

        return repository.save(alert);
    }

}
