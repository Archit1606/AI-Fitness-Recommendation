package com.fitness.userservice.services;

import com.fitness.userservice.UserRepository;
import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.dto.UserPreferenceUpdateRequest;
import com.fitness.userservice.event.CacheInvalidationEvent;
import com.fitness.userservice.event.CacheInvalidationType;
import com.fitness.userservice.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServices {
    private final UserRepository repository;
    private final KafkaTemplate<String, CacheInvalidationEvent> kafkaTemplate;

    @Value("${kafka.topic.cache-invalidation}")
    private String cacheInvalidationTopic;
    public UserResponse register(RegisterRequest request) {
        if (repository.existsByEmail(request.getEmail())){
            User existingUser = repository.findByEmail(request.getEmail());
            return mapToResponse(existingUser);
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setKeycloakId(request.getKeycloakId());
        user.setLastName(request.getLastName());
        user.setPassword(request.getPassword());
        user.setFitnessGoal(request.getFitnessGoal());
        user.setDailyCalorieTarget(request.getDailyCalorieTarget());


        User savedUser = repository.save(user);
        return mapToResponse(savedUser);
    }

    public UserResponse getUserProfile(String userId) {
        User user=repository.findById(userId)
            .orElseThrow(()->new RuntimeException("user not found"));
        return mapToResponse(user);
    }




    public Boolean existByKeycloakId(String userId) {
        log.info("calling User Service for {}",userId);
        return repository.existsByKeycloakId(userId);

    }

    public UserResponse updatePreferences(String keycloakId, UserPreferenceUpdateRequest request) {
        User user = repository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("user not found"));

        boolean updated = false;
        if (request.getFitnessGoal() != null && !Objects.equals(user.getFitnessGoal(), request.getFitnessGoal())) {
            user.setFitnessGoal(request.getFitnessGoal());
            publishInvalidation(keycloakId, CacheInvalidationType.FITNESS_GOAL_UPDATED);
            updated = true;
        }
        if (request.getDailyCalorieTarget() != null
                && !Objects.equals(user.getDailyCalorieTarget(), request.getDailyCalorieTarget())) {
            user.setDailyCalorieTarget(request.getDailyCalorieTarget());
            publishInvalidation(keycloakId, CacheInvalidationType.CALORIE_TARGET_CHANGED);
            updated = true;
        }

        if (updated) {
            User savedUser = repository.save(user);
            return mapToResponse(savedUser);
        }

        return mapToResponse(user);
    }

    private void publishInvalidation(String userId, CacheInvalidationType type) {
        try {
            CacheInvalidationEvent event = CacheInvalidationEvent.builder()
                    .userId(userId)
                    .type(type)
                    .sourceService("user-service")
                    .occurredAt(Instant.now())
                    .build();
            kafkaTemplate.send(cacheInvalidationTopic, userId, event);
            log.info("Cache invalidation event published for user {} ({})", userId, type);
        } catch (Exception e) {
            log.warn("Failed to publish cache invalidation event for user {}: {}", userId, e.getMessage());
        }
    }

    private UserResponse mapToResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setPassword(user.getPassword());
        userResponse.setKeycloakId(user.getKeycloakId());
        userResponse.setEmail(user.getEmail());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        userResponse.setFitnessGoal(user.getFitnessGoal());
        userResponse.setDailyCalorieTarget(user.getDailyCalorieTarget());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());
        return userResponse;
    }
}
