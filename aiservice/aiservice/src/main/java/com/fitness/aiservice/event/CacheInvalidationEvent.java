package com.fitness.aiservice.event;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheInvalidationEvent {
    private String userId;
    private CacheInvalidationType type;
    private String sourceService;
    private Instant occurredAt;
}
