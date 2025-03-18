package com.example.userlevelrps.filter;

import com.example.userlevelrps.exception.ApplicationExceptions;
import com.example.userlevelrps.exception.UserIdNotFoundException;
import com.example.userlevelrps.model.RateLimit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilterV2 implements WebFilter {
    protected static final Map<String, Integer> USER_RATE_LIMITS = new HashMap<>();

    static {
        USER_RATE_LIMITS.put("user1", 30);
        USER_RATE_LIMITS.put("user2", 20);
    }

    @Value("${user.ratelimit.ms}")
    public int periodMillis;

    private final Map<String, RateLimit> limitMap = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String userId = request.getHeaders().getFirst("X-User-Id");
        return Mono.fromSupplier(() -> userId)
                .switchIfEmpty(ApplicationExceptions.userIdNotFound())
                .flatMap(user -> helper(user, exchange, chain))
                .onErrorResume(UserIdNotFoundException.class, e -> Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST)));
    }

    public Mono<Void> helper(String userId, ServerWebExchange exchange, WebFilterChain chain) {
        if (!USER_RATE_LIMITS.containsKey(userId)) {
            return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
        }
        Integer allowedRequests = USER_RATE_LIMITS.get(userId);
        RateLimit rateLimit = limitMap.computeIfAbsent(userId, key -> new RateLimit(allowedRequests, periodMillis));
        if (rateLimit.tryAcquire()) {
            return chain.filter(exchange);
        }
        return Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS));
    }
}
