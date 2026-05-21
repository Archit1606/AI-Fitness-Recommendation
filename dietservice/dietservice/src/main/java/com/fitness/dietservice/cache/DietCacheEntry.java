package com.fitness.dietservice.cache;

import com.fitness.dietservice.dto.DietRecommendationResponse;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietCacheEntry {
    private DietRecommendationResponse recommendation;
    private Instant cachedAt;
}
