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
public class HealthzController {

    @Autowired
    private SecurityAlertRepository repository;

    @Autowired
    private org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

    @GetMapping("/healthz")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("app", "ok");

        // Verify PostgreSQL Connectivity
        try {
            long c = repository.count();
            status.put("db", "ok");
            status.put("alerts_count", c);
        } catch (Exception e) {
            status.put("db", "error");
            status.put("db_error", e.getMessage());
        }

        // Verify Valkey (Redis) Connectivity
        try {
            String pingResponse = redisTemplate.getConnectionFactory().getConnection().ping();
            status.put("valkey", "ok");
            status.put("valkey_ping", pingResponse);
        } catch (Exception e) {
            status.put("valkey", "error");
            status.put("valkey_error", e.getMessage());
        }

        return ResponseEntity.ok(status);
    }
}
