package com.edos.Middleware.controller;

import com.edos.Middleware.config.SecurityUtils;
import com.edos.Middleware.dto.NetworkFlowInput;
import com.edos.Middleware.service.IngestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ingest")
public class IngestionController {

    @Autowired
    private IngestionService ingestionService;

    @PostMapping("/flow")
    public ResponseEntity<?> ingestFlow(
            @RequestBody NetworkFlowInput flow,
            @RequestParam String resourceId,
            @RequestParam String sourceIp) {
        Long userId = SecurityUtils.getAuthenticatedUser().getId();
        ingestionService.processFlow(flow, userId, resourceId, sourceIp);
        return ResponseEntity.accepted().body(Map.of("status", "received", "message", "Flow is being processed"));
    }

    @PostMapping("/batch")
    public ResponseEntity<?> ingestBatch(
            @RequestBody List<NetworkFlowInput> flows,
            @RequestParam String resourceId,
            @RequestParam String sourceIp) {
        Long userId = SecurityUtils.getAuthenticatedUser().getId();
        ingestionService.processBatch(flows, userId, resourceId, sourceIp);
        return ResponseEntity.accepted().body(Map.of("status", "received", "message", "Batch is being processed", "count", flows.size()));
    }
}
