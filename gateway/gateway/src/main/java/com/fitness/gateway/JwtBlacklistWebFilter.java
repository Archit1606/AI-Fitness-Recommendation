package com.fitness.gateway;

import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
@RequiredArgsConstructor
@Slf4j
public class JwtBlacklistWebFilter implements WebFilter {
    private final JwtBlacklistService blacklistService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = extractToken(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
        if (token == null) {
            return chain.filter(exchange);
        }

        return blacklistService.isBlacklisted(token)
                .flatMap(blacklisted -> {
                    if (!blacklisted) {
                        return chain.filter(exchange);
                    }
                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    byte[] body = "{\"error\":\"token_revoked\",\"message\":\"Token has been revoked.\"}"
                            .getBytes(StandardCharsets.UTF_8);
                    return response.writeWith(Mono.just(response.bufferFactory().wrap(body)));
                })
                .onErrorResume(ex -> {
                    log.warn("JWT blacklist check failed: {}", ex.getMessage());
                    return chain.filter(exchange);
                });
    }

    private String extractToken(String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            return null;
        }
        if (authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7).trim();
        }
        return null;
    }
}
