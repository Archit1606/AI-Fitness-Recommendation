package com.fitness.aiservice.cache;

import com.fitness.aiservice.model.Recommendation;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationCacheEntry {
    private List<Recommendation> recommendations;
    private Instant cachedAt;
}
