package com.edos.Middleware.service;

import com.edos.Middleware.dto.Alert;
import com.edos.Middleware.dto.AlertResponse;

import java.time.LocalDateTime;

public class AlertService {

    public AlertResponse Alert(Alert request) {

        LocalDateTime timestamp = request.getTimestamp();
        String alert_id = request.getAlertId();


        AlertResponse response = new AlertResponse();

        response.setAlert_id(request.getAlertId());
        response.setStatus("Okay");


        return response;
    }

}
