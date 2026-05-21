package com.fitness.dietservice.cache;

import com.fitness.dietservice.config.CacheProperties;
import com.fitness.dietservice.dto.DietRecommendationResponse;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DietCacheService {
    private final RedisTemplate<String, DietCacheEntry> redisTemplate;
    private final CacheProperties cacheProperties;

    public Optional<DietRecommendationResponse> getLatest(String userId) {
        String key = DietCacheKey.forUser(userId);
        try {
            DietCacheEntry entry = redisTemplate.opsForValue().get(key);
            if (entry == null || entry.getRecommendation() == null) {
                log.info("Diet cache miss for user {}", userId);
                return Optional.empty();
            }
            log.info("Diet cache hit for user {}", userId);
            return Optional.of(entry.getRecommendation());
        } catch (Exception ex) {
            log.warn("Diet cache read failed for user {}: {}", userId, ex.getMessage());
            return Optional.empty();
        }
    }

    public void putLatest(String userId, DietRecommendationResponse response) {
        String key = DietCacheKey.forUser(userId);
        try {
            DietCacheEntry entry = DietCacheEntry.builder()
                    .recommendation(response)
                    .cachedAt(Instant.now())
                    .build();
            redisTemplate.opsForValue().set(key, entry, cacheProperties.getTtl());
            log.info("Diet cache updated for user {} with ttl {}", userId, cacheProperties.getTtl());
        } catch (Exception ex) {
            log.warn("Diet cache write failed for user {}: {}", userId, ex.getMessage());
        }
    }

    public void evictLatest(String userId) {
        String key = DietCacheKey.forUser(userId);
        try {
            Boolean removed = redisTemplate.delete(key);
            if (Boolean.TRUE.equals(removed)) {
                log.info("Diet cache evicted for user {}", userId);
            } else {
                log.info("Diet cache eviction skipped for user {} (not found)", userId);
            }
        } catch (Exception ex) {
            log.warn("Diet cache eviction failed for user {}: {}", userId, ex.getMessage());
        }
    }
}
