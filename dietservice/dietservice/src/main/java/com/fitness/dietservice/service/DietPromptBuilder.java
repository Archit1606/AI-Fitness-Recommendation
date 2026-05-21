package com.fitness.dietservice.service;

import com.fitness.dietservice.dto.DietRecommendationRequest;
import java.util.List;
import java.util.StringJoiner;
import org.springframework.stereotype.Component;

@Component
public class DietPromptBuilder {

    public String buildPrompt(String userId, DietRecommendationRequest request) {
        String allergies = formatAllergies(request.getAllergies());

        return String.format("""
                You are a certified nutritionist. Create a personalized diet plan in STRICT JSON format.
                Use the user profile below and return ONLY valid JSON (no markdown, no code fences).

                Required JSON format:
                {
                  "summary": "Short plan summary",
                  "meals": {
                    "breakfast": "...",
                    "lunch": "...",
                    "dinner": "...",
                    "snacks": "..."
                  },
                  "calories": {
                    "total": 2000,
                    "breakfast": 500,
                    "lunch": 600,
                    "dinner": 700,
                    "snacks": 200
                  },
                  "macros": {
                    "protein_g": 140,
                    "carbs_g": 220,
                    "fat_g": 60
                  },
                  "hydration": "Hydration recommendation"
                }

                User profile:
                - userId: %s
                - age: %d
                - gender: %s
                - height_cm: %.2f
                - weight_kg: %.2f
                - fitness_goal: %s
                - allergies: %s
                - preference: %s
                - activity_level: %s
                - target_calories: %d

                Constraints:
                - Respect allergies and food preference (veg/non-veg).
                - Keep meals realistic and healthy.
                - Ensure calories sum close to target calories.
                """,
                userId,
                request.getAge(),
                request.getGender(),
                request.getHeightCm(),
                request.getWeightKg(),
                request.getFitnessGoal(),
                allergies,
                request.getPreference(),
                request.getActivityLevel(),
                request.getTargetCalories()
        );
    }

    private String formatAllergies(List<String> allergies) {
        if (allergies == null || allergies.isEmpty()) {
            return "none";
        }
        StringJoiner joiner = new StringJoiner(", ");
        allergies.forEach(joiner::add);
        return joiner.toString();
    }
}
