package com.fitness.dietservice.controller;

import com.fitness.dietservice.dto.DietRecommendationRequest;
import com.fitness.dietservice.dto.DietRecommendationResponse;
import com.fitness.dietservice.service.DietRecommendationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/diet-recommendations")
@RequiredArgsConstructor
public class DietRecommendationController {
    private final DietRecommendationService dietRecommendationService;

    @PostMapping
    public ResponseEntity<DietRecommendationResponse> createRecommendation(
            @RequestHeader("X-User-ID") String userId,
            @Valid @RequestBody DietRecommendationRequest request) {
        return ResponseEntity.ok(dietRecommendationService.generateRecommendation(userId, request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DietRecommendationResponse>> getUserRecommendations(
            @PathVariable String userId) {
        return ResponseEntity.ok(dietRecommendationService.getUserRecommendations(userId));
    }

    @GetMapping("/user/{userId}/latest")
    public ResponseEntity<DietRecommendationResponse> getLatestRecommendation(
            @PathVariable String userId) {
        return dietRecommendationService.getLatestRecommendation(userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
