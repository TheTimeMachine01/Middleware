package com.edos.Middleware.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class Alert {
    private LocalDateTime timestamp;
    private String alertId;
    private Event event;
    private Source source;
    private Destination destination;
    private Model model;
    private String rawLogRef;
    private String recommendation;
    private SystemInfo system;

}
