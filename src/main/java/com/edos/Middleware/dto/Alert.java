package com.edos.Middleware.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class Alert {
    private String id;
    private String timestamp;
    private String type;
    private Model model;
    private Event event;
    private Source source;
    private Source destination;

}
