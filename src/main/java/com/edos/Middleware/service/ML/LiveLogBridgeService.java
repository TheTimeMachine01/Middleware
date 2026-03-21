package com.edos.Middleware.service.ML;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class LiveLogBridgeService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void receiveMessage(String message) {
        // Forward the raw JSON log from Redis to the frontend via WebSockets
        messagingTemplate.convertAndSend("/topic/ml-live-logs", message);
    }
}
