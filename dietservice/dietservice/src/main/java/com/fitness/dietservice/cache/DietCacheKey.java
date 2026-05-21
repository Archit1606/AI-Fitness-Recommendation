package com.fitness.dietservice.cache;

public final class DietCacheKey {
    private static final String USER_PREFIX = "diet:user:";

    private DietCacheKey() {
    }

    public static String forUser(String userId) {
        return USER_PREFIX + userId;
    }
}
