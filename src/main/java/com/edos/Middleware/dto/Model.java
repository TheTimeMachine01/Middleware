package com.edos.Middleware.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Model {
    private String name;
    private String version;
    private double probability;
    private double threshold;
    @JsonProperty("features_used")
    private List<String> featuresUsed;
}
