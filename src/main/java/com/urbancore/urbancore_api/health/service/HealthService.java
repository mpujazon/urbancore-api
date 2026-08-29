package com.urbancore.urbancore_api.health.service;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class HealthService {

    public String now() {
        return Instant.now().toString();
    }
}
