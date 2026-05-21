package com.fitness.dietservice.model;

import com.fitness.dietservice.dto.CalorieBreakdown;
import com.fitness.dietservice.dto.MacroBreakdown;
import com.fitness.dietservice.dto.MealPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietPlan {
    private String summary;
    private MealPlan meals;
    private CalorieBreakdown calories;
    private MacroBreakdown macros;
    private String hydration;
}
