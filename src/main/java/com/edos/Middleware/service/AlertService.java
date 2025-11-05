package com.edos.Middleware.service;

import com.edos.Middleware.dto.Alert;
import com.edos.Middleware.dto.AlertResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;

@Service
public class AlertService {
    @Autowired
    private ObjectMapper objectMapper;

    public Alert getLatestAlert() throws Exception {
        ClassPathResource resource = new ClassPathResource("data.json");
        try (InputStream in = resource.getInputStream()) {
            return objectMapper.readValue(in, Alert.class);
        }
    }

//    public AlertResponse Alert(Alert request) {
//
//        LocalDateTime timestamp = request.getTimestamp();
//        String alert_id = request.getAlertId();
//
//
//        AlertResponse response = new AlertResponse();
//
//        response.setAlert_id(request.getAlertId());
//        response.setStatus("Okay");
//
//
//        return response;
//    }
}
