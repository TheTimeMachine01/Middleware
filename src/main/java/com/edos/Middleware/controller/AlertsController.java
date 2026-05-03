package com.edos.Middleware.controller;

import com.edos.Middleware.dto.ML.MLAlertRequest;
import com.edos.Middleware.dto.ML.MLAlertResponse;
import com.edos.Middleware.dto.ML.MLPredictionRequest;
import com.edos.Middleware.dto.SecurityAlertDto;
import com.edos.Middleware.entity.AlertStatus;
import com.edos.Middleware.service.AlertService;
import com.edos.Middleware.config.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/alerts")
public class AlertsController {

    @Autowired
    private AlertService alertService;

    @Autowired
    private SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<Page<SecurityAlertDto>> getAlerts(
            @RequestParam(required = false) String level,
            @RequestParam(required = false) Boolean read,
            Pageable pageable) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(alertService.findAlertsForUser(userId, level, read, pageable));
    }

    @GetMapping("/latest")
    public ResponseEntity<com.edos.Middleware.dto.Alert> getLatestAlert() throws Exception {
        return ResponseEntity.ok(alertService.getLatestAlert());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SecurityAlertDto> getAlertById(@PathVariable UUID id) {
        Long userId = securityUtils.getCurrentUserId();
        return alertService.findAlertById(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(alertService.getAlertStats(userId));
    }

    @PostMapping("/ml-prediction")
    public ResponseEntity<MLAlertResponse> createFromML(@RequestBody MLAlertRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        return alertService.createAlertFromMLPrediction(request, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/batch-ml-predictions")
    public ResponseEntity<com.edos.Middleware.dto.BatchPredictionResponseDto> createBatchFromML(@RequestBody List<MLPredictionRequest> requests) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(alertService.createAlertsFromBatchPredictions(requests, userId));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createManualAlert(@RequestBody Map<String, Object> payload) {
        Long userId = securityUtils.getCurrentUserId();
        var alert = alertService.createManualAlert(payload, userId);
        return ResponseEntity.ok(Map.of("status", "alert_created", "id", alert.getId()));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable UUID id) {
        Long userId = securityUtils.getCurrentUserId();
        if (alertService.markAsRead(id, userId)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markAllRead() {
        Long userId = securityUtils.getCurrentUserId();
        alertService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable UUID id) {
        Long userId = securityUtils.getCurrentUserId();
        if (alertService.deleteAlert(id, userId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/feedback")
    public ResponseEntity<Void> provideFeedback(
            @PathVariable UUID id,
            @RequestParam String label,
            @RequestParam AlertStatus status) {
        Long userId = securityUtils.getCurrentUserId();
        if (alertService.updateFeedback(id, userId, label, status)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
