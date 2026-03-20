package com.korarwandasystem.korarwanda.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping("/check")
    public Map<String, Object> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("application", "Kora Rwanda Backend");
        response.put("message", "Application is running successfully");
        return response;
    }

    @GetMapping("/simple")
    public String simpleHealth() {
        return "OK - Application is running at " + LocalDateTime.now();
    }
}
