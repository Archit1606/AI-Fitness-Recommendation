package com.fitness.dietservice.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MacroBreakdown {
    @JsonProperty("protein_g")
    @JsonAlias({"protein", "proteinGrams"})
    private Integer proteinGrams;

    @JsonProperty("carbs_g")
    @JsonAlias({"carbs", "carbsGrams"})
    private Integer carbsGrams;

    @JsonProperty("fat_g")
    @JsonAlias({"fat", "fatGrams"})
    private Integer fatGrams;
}
