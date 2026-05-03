package com.edos.Middleware.controller;

import com.edos.Middleware.service.AlertService;
import com.edos.Middleware.config.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    @Autowired
    private AlertService alertService;

    @Autowired
    private SecurityUtils securityUtils;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics() {
        Long userId = securityUtils.getCurrentUserId();
        Map<String, Object> stats = alertService.getAlertStats(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        response.put("active_threats", stats.getOrDefault("unread", 0));
        response.put("total_attacks_detected", stats.getOrDefault("total", 0));
        
        // Simulated metrics based on alert activity
        long total = (long) stats.getOrDefault("total", 0L);
        response.put("blocked_attacks", total > 0 ? total - 2 : 0);
        response.put("total_requests", total * 100 + ThreadLocalRandom.current().nextInt(500));
        response.put("network_traffic", String.format("%.1f GB", total * 0.5 + 1.2));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/system")
    public ResponseEntity<Map<String, Object>> getSystemMetrics() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", Instant.now());
        
        Map<String, Object> system = new HashMap<>();
        system.put("cpu_usage", ThreadLocalRandom.current().nextInt(30, 85));
        system.put("memory_usage", ThreadLocalRandom.current().nextInt(40, 90));
        system.put("disk_usage", 45);
        system.put("network_io", ThreadLocalRandom.current().nextDouble(50.0, 500.0));
        
        response.put("system", system);
        return ResponseEntity.ok(response);
    }
}
