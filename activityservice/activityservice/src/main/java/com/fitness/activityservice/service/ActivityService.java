package com.fitness.activityservice.service;

import com.fitness.activityservice.ActivityRepository;
import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.event.CacheInvalidationEvent;
import com.fitness.activityservice.event.CacheInvalidationType;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.model.ActivityType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final UserValidationService userValidationService;
    private final KafkaTemplate<String ,Activity>kafkaTemplate;
    private final KafkaTemplate<String, CacheInvalidationEvent> cacheInvalidationKafkaTemplate;
    @Value("${kafka.topic.name}")
    private String topicName;
    @Value("${kafka.topic.cache-invalidation}")
    private String cacheInvalidationTopic;

     public ActivityResponse trackActivity(ActivityRequest request) {

        boolean isValidUser= userValidationService.validateUser(request.getUserId());
        if(!isValidUser){
            throw new RuntimeException("Invalid User: "+ request.getUserId());
        }
         Activity activity= Activity.builder()
                 .userId(request.getUserId())
                 .type(request.getType())
                 .duration(request.getDuration())
                 .caloriesBurned(request.getCaloriesBurned())
                 .startTime(request.getStartTime())
                 .additionalMetrics(request.getAdditionalMetrics())
                 .build();


         Activity savedActivity=activityRepository.save(activity);

        try {
            kafkaTemplate.send(topicName, savedActivity.getUserId(), savedActivity);
        } catch (Exception e) {
            log.warn("Failed to publish activity event for user {}: {}", savedActivity.getUserId(), e.getMessage());
        }

        publishCacheInvalidation(savedActivity.getUserId());
         return mapToResponse(savedActivity);


    }

    private ActivityResponse mapToResponse(Activity activity) {
         ActivityResponse response = new ActivityResponse();
         response.setId(activity.getId());
         response.setUserId(activity.getUserId());
         response.setDuration(activity.getDuration());
         response.setCaloriesBurned(activity.getCaloriesBurned());
         response.setStartTime(activity.getStartTime());
         response.setAdditionalMetrics(activity.getAdditionalMetrics());
         response.setCreatedAt(activity.getCreatedAt());
         response.setUpdatedAt(activity.getUpdatedAt());
         return response;
    }

    public List<ActivityResponse> getUserActivities(String userId) {
        List<Activity> activityList= activityRepository.findByUserId(userId);
        return activityList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void publishCacheInvalidation(String userId) {
        try {
            CacheInvalidationEvent event = CacheInvalidationEvent.builder()
                    .userId(userId)
                    .type(CacheInvalidationType.WORKOUT_HISTORY_CHANGED)
                    .sourceService("activity-service")
                    .occurredAt(Instant.now())
                    .build();
            cacheInvalidationKafkaTemplate.send(cacheInvalidationTopic, userId, event);
            log.info("Cache invalidation event published for user {}", userId);
        } catch (Exception e) {
            log.warn("Failed to publish cache invalidation event for user {}: {}", userId, e.getMessage());
        }
    }
}
