package com.fitness.dietservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
public class DietRecommendationRequest {
    @NotNull
    @Min(10)
    @Max(100)
    private Integer age;

    @NotBlank
    private String gender;

    @NotNull
    @Min(50)
    @Max(250)
    private Double heightCm;

    @NotNull
    @Min(20)
    @Max(300)
    private Double weightKg;

    @NotBlank
    private String fitnessGoal;

    @Size(max = 10)
    private List<String> allergies;

    @NotBlank
    private String preference;

    @NotBlank
    private String activityLevel;

    @NotNull
    @Min(800)
    @Max(6000)
    private Integer targetCalories;
}
