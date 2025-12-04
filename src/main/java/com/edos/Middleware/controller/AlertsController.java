package com.edos.Middleware.controller;

import com.edos.Middleware.config.SecurityUtils;
import com.edos.Middleware.dto.Alert;
import com.edos.Middleware.dto.BatchPredictionResponseDto;
import com.edos.Middleware.dto.ML.MLAlertRequest;
import com.edos.Middleware.dto.ML.MLAlertResponse;
import com.edos.Middleware.dto.ML.MLPredictionRequest;
import com.edos.Middleware.dto.SecurityAlertDto;
import com.edos.Middleware.entity.SecurityAlert;
import com.edos.Middleware.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertsController {

    @Autowired
    private AlertService alertService;

    @GetMapping("/latest")
    public ResponseEntity<Alert> getLatestAlert() throws Exception {
        Alert alert = alertService.getLatestAlert();
        return ResponseEntity.ok(alert);
    }

    @PostMapping("/ml-prediction")
    public ResponseEntity<?> createAlertFromPrediction(@RequestBody MLAlertRequest request) {
        Long userId = SecurityUtils.getAuthenticatedUser().getId();
        Optional<MLAlertResponse> response = alertService.createAlertFromMLPrediction(request, userId);

        return response.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of("message", "Prediction processed. No alert created as is_attack was false.")));
    }

    @GetMapping
    public ResponseEntity<List<SecurityAlertDto>> listAlerts(
            @RequestParam Optional<String> level,
            @RequestParam Optional<Boolean> read,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        Long userId = SecurityUtils.getAuthenticatedUser().getId();
        int pageNumber = Math.max(0, offset / Math.max(1, limit));
        Pageable pageable = PageRequest.of(pageNumber, Math.max(1, limit), Sort.by(Sort.Direction.DESC, "detectedAt"));
        Page<SecurityAlertDto> page = alertService.findAlertsForUser(userId, level.orElse(null), read.orElse(null), pageable);
        return ResponseEntity.ok(page.getContent());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createManualAlert(@RequestBody Map<String, Object> payload) {
        Long userId = SecurityUtils.getAuthenticatedUser().getId();
        SecurityAlert createdAlert = alertService.createManualAlert(payload, userId);

        Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("status", "alert_created");
        response.put("id", createdAlert.getId().toString());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PostMapping("/batch-ml-predictions")
    public ResponseEntity<BatchPredictionResponseDto> createBatchAlerts(
            @RequestBody List<MLPredictionRequest> requests) {
        Long userId = SecurityUtils.getAuthenticatedUser().getId();
        BatchPredictionResponseDto response = alertService.createAlertsFromBatchPredictions(requests, userId);
        return ResponseEntity.ok(response);
    }


}