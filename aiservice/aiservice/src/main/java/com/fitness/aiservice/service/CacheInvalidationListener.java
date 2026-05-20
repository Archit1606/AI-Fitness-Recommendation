package com.fitness.aiservice.service;

import com.fitness.aiservice.cache.RecommendationCacheService;
import com.fitness.aiservice.event.CacheInvalidationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheInvalidationListener {
    private final RecommendationCacheService cacheService;

    @KafkaListener(
            topics = "${kafka.topic.cache-invalidation}",
            groupId = "cache-invalidator-group",
            properties = {
                    "spring.json.value.default.type=com.fitness.aiservice.event.CacheInvalidationEvent",
                    "spring.json.use.type.headers=false"
            }
    )
    public void handleInvalidation(CacheInvalidationEvent event) {
        if (event == null || event.getUserId() == null || event.getUserId().isBlank()) {
            log.warn("Cache invalidation event ignored due to missing userId");
            return;
        }
        cacheService.evictUserRecommendations(event.getUserId());
        log.info("Cache invalidation processed for user {} ({})", event.getUserId(), event.getType());
    }
}
