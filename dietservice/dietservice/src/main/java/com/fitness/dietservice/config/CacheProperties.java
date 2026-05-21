package com.fitness.dietservice.config;

import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "cache.diet")
public class CacheProperties {
    private Duration ttl = Duration.ofMinutes(30);
}
