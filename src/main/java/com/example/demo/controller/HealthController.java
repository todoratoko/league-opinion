package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    /**
     * Simple health check endpoint to keep server awake
     * Can be pinged by external services like cron-job.org or UptimeRobot
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("service", "League Opinion Backend");
        return ResponseEntity.ok(response);
    }

    /**
     * Detailed health check with system info
     */
    @GetMapping("/health/detailed")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("service", "League Opinion Backend");

        // System info
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> system = new HashMap<>();
        system.put("processors", runtime.availableProcessors());
        system.put("freeMemory", runtime.freeMemory() / 1024 / 1024 + " MB");
        system.put("totalMemory", runtime.totalMemory() / 1024 / 1024 + " MB");
        system.put("maxMemory", runtime.maxMemory() / 1024 / 1024 + " MB");

        response.put("system", system);
        return ResponseEntity.ok(response);
    }
}
