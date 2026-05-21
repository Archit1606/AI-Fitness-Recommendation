package com.fitness.dietservice.dto;

import com.fitness.dietservice.model.DietPlan;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DietRecommendationResponse {
    private String id;
    private String userId;
    private DietPlan plan;
    private LocalDateTime createdAt;

    private Integer age;
    private String gender;
    private Double heightCm;
    private Double weightKg;
    private String fitnessGoal;
    private String preference;
    private String activityLevel;
    private Integer targetCalories;
}
