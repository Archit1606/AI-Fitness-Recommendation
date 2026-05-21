package com.fitness.dietservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.dietservice.dto.DietRecommendationRequest;
import com.fitness.dietservice.dto.DietRecommendationResponse;
import com.fitness.dietservice.model.DietPlan;
import com.fitness.dietservice.model.DietRecommendation;
import com.fitness.dietservice.repository.DietRecommendationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DietRecommendationService {
    private final DietRecommendationRepository repository;
    private final DietPromptBuilder promptBuilder;
    private final GeminiService geminiService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public DietRecommendationResponse generateRecommendation(String userId, DietRecommendationRequest request) {
        String prompt = promptBuilder.buildPrompt(userId, request);
        String aiResponse = geminiService.getDietPlan(prompt);
        DietPlan plan = parseDietPlan(aiResponse);

        DietRecommendation recommendation = DietRecommendation.builder()
                .userId(userId)
                .age(request.getAge())
                .gender(request.getGender())
                .heightCm(request.getHeightCm())
                .weightKg(request.getWeightKg())
                .fitnessGoal(request.getFitnessGoal())
                .allergies(request.getAllergies())
                .preference(request.getPreference())
                .activityLevel(request.getActivityLevel())
                .targetCalories(request.getTargetCalories())
                .plan(plan)
                .build();

        DietRecommendation saved = repository.save(recommendation);
        log.info("Diet recommendation created for user {}", userId);
        return mapToResponse(saved);
    }

    public List<DietRecommendationResponse> getUserRecommendations(String userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private DietPlan parseDietPlan(String aiResponse) {
        try {
            String jsonPayload = extractJsonPayload(aiResponse);
            return objectMapper.readValue(jsonPayload, DietPlan.class);
        } catch (Exception ex) {
            log.warn("Failed to parse diet plan: {}", ex.getMessage());
            return DietPlan.builder()
                    .summary("Unable to generate a detailed diet plan at the moment")
                    .meals(null)
                    .calories(null)
                    .macros(null)
                    .hydration("Stay hydrated and aim for 2-3 liters of water daily")
                    .build();
        }
    }

    private String extractJsonPayload(String aiResponse) throws Exception {
        JsonNode root = objectMapper.readTree(aiResponse);
        JsonNode textNode = root.path("candidates")
                .path(0)
                .path("content")
                .path("parts")
                .path(0)
                .path("text");

        if (textNode.isMissingNode() || textNode.asText().isBlank()) {
            return aiResponse;
        }

        return textNode.asText()
                .replace("```json", "")
                .replace("```", "")
                .trim();
    }

    private DietRecommendationResponse mapToResponse(DietRecommendation recommendation) {
        return DietRecommendationResponse.builder()
                .id(recommendation.getId())
                .userId(recommendation.getUserId())
                .plan(recommendation.getPlan())
                .createdAt(recommendation.getCreatedAt())
                .age(recommendation.getAge())
                .gender(recommendation.getGender())
                .heightCm(recommendation.getHeightCm())
                .weightKg(recommendation.getWeightKg())
                .fitnessGoal(recommendation.getFitnessGoal())
                .preference(recommendation.getPreference())
                .activityLevel(recommendation.getActivityLevel())
                .targetCalories(recommendation.getTargetCalories())
                .build();
    }
}
