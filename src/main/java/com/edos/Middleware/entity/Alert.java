package com.edos.Middleware.entity;

import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(name = "alert")
public class Alert {
    private Long id;
    private Integer event_id; // from event entity
    private LocalDateTime timestamp;

    private Integer source_ip;
    private Integer source_port;
    private String country_iso;
    private String region;
    private String city;

    private Integer dest_ip;
    private Integer dest_port;

    private String model_name;
    private String model_version;
    private Integer probablity;
    private Integer threshold;
    private String features_used;

}
