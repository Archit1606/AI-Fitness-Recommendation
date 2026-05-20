package com.fitness.gateway;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtBlacklistService {
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    private final ReactiveStringRedisTemplate redisTemplate;

    public Mono<Void> blacklistToken(String token) {
        if (token == null || token.isBlank()) {
            return Mono.empty();
        }
        return Mono.fromCallable(() -> parseClaims(token))
                .flatMap(claims -> {
                    Date exp = claims.getExpirationTime();
                    if (exp == null) {
                        return Mono.empty();
                    }
                    Instant expiresAt = exp.toInstant();
                    Duration ttl = Duration.between(Instant.now(), expiresAt);
                    if (ttl.isNegative() || ttl.isZero()) {
                        return Mono.empty();
                    }
                    String key = blacklistKey(token);
                    return redisTemplate.opsForValue().set(key, "1", ttl).then();
                })
                .onErrorResume(ex -> {
                    log.warn("Failed to blacklist token: {}", ex.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> isBlacklisted(String token) {
        if (token == null || token.isBlank()) {
            return Mono.just(false);
        }
        String key = blacklistKey(token);
        return redisTemplate.hasKey(key)
                .onErrorResume(ex -> {
                    log.warn("Failed to check blacklist: {}", ex.getMessage());
                    return Mono.just(false);
                });
    }

    private JWTClaimsSet parseClaims(String token) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.getJWTClaimsSet();
    }

    private String blacklistKey(String token) {
        return BLACKLIST_PREFIX + sha256(token);
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to hash token", ex);
        }
    }
}
