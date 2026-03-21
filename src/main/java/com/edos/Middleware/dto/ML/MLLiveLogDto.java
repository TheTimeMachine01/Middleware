package com.edos.Middleware.dto.ML;

import lombok.Data;
import java.util.Map;

@Data
public class MLLiveLogDto {
    private String message_id;
    private String timestamp;
    private String client_id;
    private String resource_id;
    private Map<String, Object> flow;
    private Map<String, Object> prediction;
    private String source;
}
