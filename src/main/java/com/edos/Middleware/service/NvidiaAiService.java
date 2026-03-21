package com.edos.Middleware.service;

import com.edos.Middleware.dto.NetworkFlowInput;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
public class NvidiaAiService {

    @Value("${nvidia.api.key}")
    private String apiKey;

    @Value("${nvidia.api.base-url}")
    private String baseUrl;

    @Value("${nvidia.api.model}")
    private String model;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public NvidiaAiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> analyzeThreat(NetworkFlowInput flow, String sourceIp) {
        log.info("Requesting Nvidia AI analysis for flow from {}", sourceIp);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            String prompt = String.format(
                "Analyze the following network flow data for security threats (especially EDoS or DDoS attacks):\n" +
                "Source IP: %s\n" +
                "Flow Stats: Duration=%f, Fwd Pkts=%d, Bwd Pkts=%d, Fwd Max Len=%d, Pkts/s=%f, Bytes/s=%f\n" +
                "Provide the response ONLY in a valid JSON format with keys: 'is_attack' (boolean), 'attack_type' (string), 'confidence' (float 0-1), 'explanation' (string), and 'severity' (CRITICAL/HIGH/MEDIUM/LOW).",
                sourceIp, flow.getFlowDuration(), flow.getTotFwdPkts(), flow.getTotBwdPkts(), flow.getFwdPktLenMax(), flow.getFlowPktsS(), flow.getFlowBytsS()
            );

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "You are a cybersecurity expert specialized in EDoS and DDoS detection."),
                Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("temperature", 0.2);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(baseUrl + "/chat/completions", entity, String.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
                Map<String, Object> fullResponse = objectMapper.readValue(responseEntity.getBody(), new TypeReference<>() {});
                List<Map<String, Object>> choices = objectMapper.convertValue(fullResponse.get("choices"), new TypeReference<>() {});
                
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> messageMap = objectMapper.convertValue(choices.get(0).get("message"), new TypeReference<>() {});
                    String content = (String) messageMap.get("content");
                    
                    if (content != null) {
                        content = content.replaceAll("```json|```", "").trim();
                        return objectMapper.readValue(content, new TypeReference<>() {});
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to analyze threat with Nvidia AI: {}", e.getMessage());
        }

        return Map.of("is_attack", false, "explanation", "AI Analysis failed or was skipped.");
    }
}
