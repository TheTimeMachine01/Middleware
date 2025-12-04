package com.edos.Middleware.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "security_alerts")
@Data
public class SecurityAlert {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "resource_id")
    private UUID resourceId;

    @Column(name = "type")
    private String type;

    @Column(name = "category")
    private String category;

    @Column(name = "severity")
    private String severity;

    @Column(name = "title")
    private String title;

    @Column(name = "description", length = 4000)
    private String description;

    @Column(name = "source_ip")
    private String sourceIp;

    @Column(name = "target_ip")
    private String targetIp;

    @Column(name = "target_port")
    private Integer targetPort;

    @Column(name = "detection_method")
    private String detectionMethod;

    @Column(name = "confidence_score", precision = 5, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "raw_data", columnDefinition = "json")
    private String rawData;

    @Column(name = "detected_at")
    private Instant detectedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AlertStatus status;

    @Column(name = "is_read")
    private Boolean read;
}
