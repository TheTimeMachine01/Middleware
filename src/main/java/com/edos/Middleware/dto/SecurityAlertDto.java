package com.edos.Middleware.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class SecurityAlertDto {

    private UUID id;
    private String level;
    private String message;
    private String source;
    private Instant timestamp;
    private String title;
    private String category;
    private BigDecimal confidence;
    private String target_ip;
    private Integer target_port;
    private String detection_method;
    private Boolean read;
}
