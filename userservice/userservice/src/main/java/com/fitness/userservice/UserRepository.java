package com.fitness.userservice;

import com.fitness.userservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {
    Boolean existsByEmail(String email);
    Boolean existsByKeycloakId(String userId);
    User findByEmail(String email);


}
