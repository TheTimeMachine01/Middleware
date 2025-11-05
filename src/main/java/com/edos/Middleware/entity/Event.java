package com.edos.Middleware.entity;

import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "event")
public class Event {
    private int id;
    private String kind;
    private String category;
    private String action;
}
