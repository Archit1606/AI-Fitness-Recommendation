package com.fitness.aiservice.config;

import java.time.Duration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "cache.recommendation")
public class CacheProperties {
    private Duration ttl = Duration.ofMinutes(30);
}
