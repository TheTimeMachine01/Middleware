package com.edos.Middleware.controller;

import com.edos.Middleware.dto.Alert;
import com.edos.Middleware.dto.AlertResponse;
import com.edos.Middleware.dto.AlertResponse;
import com.edos.Middleware.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertController {
    @Autowired
    private AlertService alertService;

    @GetMapping("/latest")
    public ResponseEntity<Alert> getLatestAlert() throws Exception {
        Alert alert = alertService.getLatestAlert();
        return ResponseEntity.ok(alert);
    }

//    @PostMapping
//    public ResponseEntity<AlertResponse> receiveAlert(@RequestBody Alert request) {
//        AlertResponse response = alertService.Alert(request);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
}