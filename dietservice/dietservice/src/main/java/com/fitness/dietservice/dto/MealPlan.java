package com.fitness.dietservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealPlan {
    private String breakfast;
    private String lunch;
    private String dinner;
    private String snacks;
}
