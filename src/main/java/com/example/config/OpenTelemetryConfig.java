package com.example.config;

import io.opentelemetry.api.trace.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfig {
    
    @Bean
    public Tracer tracer(io.opentelemetry.api.OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer("com.example.tracer");
    }
} 