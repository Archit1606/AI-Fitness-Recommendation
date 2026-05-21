package com.fitness.dietservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalorieBreakdown {
    private Integer total;
    private Integer breakfast;
    private Integer lunch;
    private Integer dinner;
    private Integer snacks;
}
