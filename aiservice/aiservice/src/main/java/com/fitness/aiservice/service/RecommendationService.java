package com.fitness.aiservice.service;


import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.respository.RecommendationRepository;
import com.fitness.aiservice.cache.RecommendationCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final RecommendationCacheService cacheService;


    public List<Recommendation> getUserRecommendation(String userId) {
        return cacheService.getUserRecommendations(userId)
                .orElseGet(() -> {
                    List<Recommendation> recommendations = recommendationRepository.findByUserId(userId);
                    cacheService.putUserRecommendations(userId, recommendations);
                    return recommendations;
                });
    }

    public Recommendation getActivityRecommendation(String activityId) {
        return recommendationRepository.findByActivityId(activityId)
                .orElseThrow(()->new RuntimeException(("No recommendation found for this id")));

    }
}
