package com.fitness.dietservice.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "diet_recommendations")
public class DietRecommendation {
    @Id
    private String id;
    private String userId;

    private Integer age;
    private String gender;
    private Double heightCm;
    private Double weightKg;
    private String fitnessGoal;
    private List<String> allergies;
    private String preference;
    private String activityLevel;
    private Integer targetCalories;

    private DietPlan plan;

    @CreatedDate
    private LocalDateTime createdAt;
}
