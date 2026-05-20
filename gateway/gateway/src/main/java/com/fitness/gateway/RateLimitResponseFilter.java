package com.fitness.gateway;

import java.nio.charset.StandardCharsets;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RateLimitResponseFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpResponseDecorator decorated = new ServerHttpResponseDecorator(response) {
            @Override
            public Mono<Void> setComplete() {
                if (getStatusCode() == HttpStatus.TOO_MANY_REQUESTS && !response.isCommitted()) {
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    byte[] body = "{\"error\":\"rate_limit_exceeded\",\"message\":\"Too many requests. Please try again later.\"}"
                            .getBytes(StandardCharsets.UTF_8);
                    return super.writeWith(Mono.just(response.bufferFactory().wrap(body)));
                }
                return super.setComplete();
            }
        };
        return chain.filter(exchange.mutate().response(decorated).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
