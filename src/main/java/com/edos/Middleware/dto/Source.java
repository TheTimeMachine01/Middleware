package com.edos.Middleware.dto;

import lombok.Data;

@Data
public class Source {
    private String ip;
    private int port;
    private Geo geo;
}
