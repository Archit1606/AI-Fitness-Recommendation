package com.fitness.dietservice.repository;

import com.fitness.dietservice.model.DietRecommendation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DietRecommendationRepository extends MongoRepository<DietRecommendation, String> {
    List<DietRecommendation> findByUserIdOrderByCreatedAtDesc(String userId);
    Optional<DietRecommendation> findTopByUserIdOrderByCreatedAtDesc(String userId);
}
