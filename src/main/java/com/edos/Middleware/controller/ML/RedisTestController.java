package com.edos.Middleware.controller.ML;

import com.edos.Middleware.dto.ML.MLLiveLogDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class RedisTestController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/publish-log")
    public String testLog(@RequestBody MLLiveLogDto dto) throws Exception {
        String json = objectMapper.writeValueAsString(dto);
        // Publishes to the same channel the LiveLogBridgeService is listening to
        redisTemplate.convertAndSend("ml:predictions", json);
        return "Message sent to Redis!";
    }
}
