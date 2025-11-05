package com.edos.Middleware.dto;

import lombok.Data;

@Data
public class Event {
    private String kind;
    private String category;
    private String action;
}
