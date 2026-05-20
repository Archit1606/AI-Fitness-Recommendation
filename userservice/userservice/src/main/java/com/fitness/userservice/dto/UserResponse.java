package com.fitness.userservice.dto;


import lombok.Data;


import java.time.LocalDateTime;
@Data
public class UserResponse {

    private String id;
    private String email;
    private String keycloakId;

    private String password;
    private String firstName;
    private String lastName;

    private String fitnessGoal;
    private Integer dailyCalorieTarget;


    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
