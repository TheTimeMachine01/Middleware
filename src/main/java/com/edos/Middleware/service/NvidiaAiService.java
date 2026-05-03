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
    private String defaultModel;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public enum AiModelProfile {
        EDOS_DETECTOR("You are a cybersecurity expert specialized in EDoS and DDoS detection."),
        ANOMALY_ANALYZER("You are a network traffic analyst focused on identifying unusual communication patterns."),
        THREAT_EXPLAINER("You are a security architect who explains complex network threats in simple terms.");

        private final String systemPrompt;
        AiModelProfile(String prompt) { this.systemPrompt = prompt; }
        public String getSystemPrompt() { return systemPrompt; }
    }

    public NvidiaAiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> analyzeThreat(NetworkFlowInput flow, String sourceIp) {
        return analyzeThreat(flow, sourceIp, AiModelProfile.EDOS_DETECTOR);
    }

    public Map<String, Object> analyzeThreat(NetworkFlowInput flow, String sourceIp, AiModelProfile profile) {
        log.info("Requesting Nvidia AI analysis ({}) for flow from {}", profile.name(), sourceIp);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            String userPrompt = generatePrompt(flow, sourceIp);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", defaultModel);
            requestBody.put("messages", List.of(
                Map.of("role", "system", "content", profile.getSystemPrompt()),
                Map.of("role", "user", "content", userPrompt)
            ));
            requestBody.put("temperature", 0.1);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(baseUrl + "/chat/completions", entity, String.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
                return parseAiResponse(responseEntity.getBody());
            }
        } catch (Exception e) {
            log.error("Failed to analyze threat with Nvidia AI: {}", e.getMessage());
        }

        return Map.of("is_attack", false, "explanation", "AI Analysis failed or was skipped.");
    }

    private String generatePrompt(NetworkFlowInput flow, String sourceIp) {
        return String.format(
            "Analyze the following network flow data for security threats:\n" +
            "Source IP: %s\n" +
            "Flow Stats: Duration=%f, Fwd Pkts=%d, Bwd Pkts=%d, Fwd Max Len=%d, Pkts/s=%f, Bytes/s=%f\n" +
            "Provide response ONLY in valid JSON: {'is_attack': boolean, 'attack_type': string, 'confidence': float 0-1, 'explanation': string, 'severity': 'CRITICAL/HIGH/MEDIUM/LOW'}",
            sourceIp, flow.getFlowDuration(), flow.getTotFwdPkts(), flow.getTotBwdPkts(), flow.getFwdPktLenMax(), flow.getFlowPktsS(), flow.getFlowBytsS()
        );
    }

    private Map<String, Object> parseAiResponse(String body) throws Exception {
        Map<String, Object> fullResponse = objectMapper.readValue(body, new TypeReference<>() {});
        List<Map<String, Object>> choices = objectMapper.convertValue(fullResponse.get("choices"), new TypeReference<>() {});
        
        if (choices != null && !choices.isEmpty()) {
            Map<String, Object> messageMap = objectMapper.convertValue(choices.get(0).get("message"), new TypeReference<>() {});
            String content = (String) messageMap.get("content");
            if (content != null) {
                content = content.replaceAll("```json|```", "").trim();
                return objectMapper.readValue(content, new TypeReference<>() {});
            }
        }
        throw new RuntimeException("Empty or invalid AI response choices");
    }
}
