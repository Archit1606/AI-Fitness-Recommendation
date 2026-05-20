package com.fitness.aiservice.cache;

import com.fitness.aiservice.config.CacheProperties;
import com.fitness.aiservice.model.Recommendation;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationCacheService {
    private final RedisTemplate<String, RecommendationCacheEntry> redisTemplate;
    private final CacheProperties cacheProperties;

    public Optional<List<Recommendation>> getUserRecommendations(String userId) {
        String key = RecommendationCacheKey.forUser(userId);
        try {
            RecommendationCacheEntry entry = redisTemplate.opsForValue().get(key);
            if (entry == null) {
                log.info("Recommendation cache miss for user {}", userId);
                return Optional.empty();
            }
            log.info("Recommendation cache hit for user {}", userId);
            return Optional.ofNullable(entry.getRecommendations());
        } catch (Exception ex) {
            log.warn("Recommendation cache read failed for user {}: {}", userId, ex.getMessage());
            return Optional.empty();
        }
    }

    public void putUserRecommendations(String userId, List<Recommendation> recommendations) {
        if (recommendations == null) {
            return;
        }
        String key = RecommendationCacheKey.forUser(userId);
        try {
            RecommendationCacheEntry entry = RecommendationCacheEntry.builder()
                    .recommendations(recommendations)
                    .cachedAt(Instant.now())
                    .build();
            redisTemplate.opsForValue().set(key, entry, cacheProperties.getTtl());
            log.info("Recommendation cache updated for user {} with ttl {}", userId, cacheProperties.getTtl());
        } catch (Exception ex) {
            log.warn("Recommendation cache write failed for user {}: {}", userId, ex.getMessage());
        }
    }

    public void evictUserRecommendations(String userId) {
        String key = RecommendationCacheKey.forUser(userId);
        try {
            Boolean removed = redisTemplate.delete(key);
            if (Boolean.TRUE.equals(removed)) {
                log.info("Recommendation cache evicted for user {}", userId);
            } else {
                log.info("Recommendation cache eviction skipped for user {} (not found)", userId);
            }
        } catch (Exception ex) {
            log.warn("Recommendation cache eviction failed for user {}: {}", userId, ex.getMessage());
        }
    }
}
