package com.edos.Middleware.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table(name = "alert")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Users users;

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
