package com.fitness.userservice;

import com.fitness.userservice.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
    Boolean existsByEmail(String email);
    Boolean existsByKeycloakId(String userId);
    User findByEmail(String email);
    Optional<User> findByKeycloakId(String userId);


}
