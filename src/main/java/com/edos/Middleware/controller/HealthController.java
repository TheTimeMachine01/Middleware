package com.edos.Middleware.controller;

import com.edos.Middleware.repository.SecurityAlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @Autowired
    private SecurityAlertRepository repository;

    @GetMapping("/healthz")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("app", "ok");
        try {
            long c = repository.count(); // quick DB call to verify connectivity
            status.put("db", "ok");
            status.put("alerts_count", c);
        } catch (Exception e) {
            status.put("db", "error");
            status.put("db_error", e.getMessage());
        }
        return ResponseEntity.ok(status);
    }
}

