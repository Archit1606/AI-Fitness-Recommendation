package com.fitness.aiservice.cache;

public final class RecommendationCacheKey {
    private static final String USER_PREFIX = "recommendation:user:";

    private RecommendationCacheKey() {
    }

    public static String forUser(String userId) {
        return USER_PREFIX + userId;
    }
}
