package com.fitness.userservice.dto;

import lombok.Data;

@Data
public class UserPreferenceUpdateRequest {
    private String fitnessGoal;
    private Integer dailyCalorieTarget;
}
