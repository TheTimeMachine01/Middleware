package com.edos.Middleware.controller;

import com.edos.Middleware.dto.Alert;
import com.edos.Middleware.dto.AlertResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alerts")
public class AlertController {

    @PostMapping
    public ResponseEntity<Alert> receiveAlert(@RequestBody AlertResponse request) {

        Alert alert = new Alert();

        return new ResponseEntity<>(alert, HttpStatus.OK);
    }
}