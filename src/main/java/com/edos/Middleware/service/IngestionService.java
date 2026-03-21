package com.edos.Middleware.service;

import com.edos.Middleware.dto.NetworkFlowInput;
import com.edos.Middleware.dto.ML.MLAlertRequest;
import com.edos.Middleware.dto.ML.MLPrediction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class IngestionService {

    @Autowired
    private AlertService alertService;

    @Autowired
    private NvidiaAiService nvidiaAiService;

    public void processFlow(NetworkFlowInput flow, Long userId, String resourceId, String sourceIp) {
        log.info("Processing network flow from {} for resource {}", sourceIp, resourceId);

        // 1. Call Nvidia AI Service for deep enrichment
        Map<String, Object> aiResult = nvidiaAiService.analyzeThreat(flow, sourceIp);

        // 2. If it is an attack, create a SecurityAlert
        if (Boolean.TRUE.equals(aiResult.get("is_attack"))) {
            log.warn("Nvidia AI detected a {} attack from {}", aiResult.get("attack_type"), sourceIp);
            
            MLAlertRequest alertReq = new MLAlertRequest();
            alertReq.setResourceId(java.util.UUID.fromString(resourceId));
            alertReq.setSourceIp(sourceIp);
            alertReq.setTargetIp("Enriched Analysis"); // Or provide actual target if available

            MLPrediction prediction = new MLPrediction();
            prediction.setAttack(true);
            prediction.setAttackType((String) aiResult.get("attack_type"));
            prediction.setConfidence(((Number) aiResult.get("confidence")).floatValue());
            prediction.setAttackProbability(((Number) aiResult.get("confidence")).floatValue());
            prediction.setModelVersion("Nvidia-Llama-3.1-70B");
            prediction.setDetails((String) aiResult.get("explanation"));
            
            alertReq.setPrediction(prediction);

            alertService.createAlertFromMLPrediction(alertReq, userId);
        }
    }

    public void processBatch(List<NetworkFlowInput> flows, Long userId, String resourceId, String sourceIp) {
        log.info("Processing batch of {} flows for resource {} from {}", flows.size(), resourceId, sourceIp);
        java.util.UUID resUuid = java.util.UUID.fromString(resourceId);

        // 1. Process flows in parallel to maximize AI analysis throughput
        List<com.edos.Middleware.dto.ML.MLPredictionRequest> batchRequests = flows.parallelStream()
                .map(flow -> {
                    // Call AI for each flow (best effort)
                    Map<String, Object> aiResult = nvidiaAiService.analyzeThreat(flow, "Batch-Source");
                    
                    if (Boolean.TRUE.equals(aiResult.get("is_attack"))) {
                        com.edos.Middleware.dto.ML.MLPredictionRequest req = new com.edos.Middleware.dto.ML.MLPredictionRequest();
                        req.setResourceId(resUuid);
                        req.setSource("Nvidia-AI-Batch");
                        
                        // Map the flow data
                        com.edos.Middleware.dto.ML.Flow flowDto = new com.edos.Middleware.dto.ML.Flow();
                        flowDto.setDstPort(flow.getDstPort());
                        flowDto.setFlowDuration(flow.getFlowDuration());
                        flowDto.setSrcIp(sourceIp); // Map the sourceIp from the batch request
                        
                        // Map the AI prediction
                        com.edos.Middleware.dto.ML.Prediction pred = new com.edos.Middleware.dto.ML.Prediction();
                        pred.setAttack(true);
                        pred.setAttackType((String) aiResult.get("attack_type"));
                        pred.setConfidence(((Number) aiResult.get("confidence")).floatValue());
                        pred.setDetails((String) aiResult.get("explanation"));
                        
                        req.setFlow(flowDto);
                        req.setPrediction(pred);
                        return req;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();

        // 2. Delegate to AlertService for optimized bulk database insertion and broadcasting
        if (!batchRequests.isEmpty()) {
            log.info("Batch ingestion found {} attacks. Persisting...", batchRequests.size());
            alertService.createAlertsFromBatchPredictions(batchRequests, userId);
        } else {
            log.info("Batch ingestion processed. No attacks detected.");
        }
    }
}
