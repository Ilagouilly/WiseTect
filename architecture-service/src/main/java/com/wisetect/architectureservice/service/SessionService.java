package com.wisetect.architectureservice.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Service
public class SessionService {
    private static final String SESSION_COOKIE_NAME = "wisetect_session_id";
    private static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 30; // 30 days

    public Mono<String> getOrCreateSessionId(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getCookies().getFirst(SESSION_COOKIE_NAME))
                .map(cookie -> cookie.getValue())
                .switchIfEmpty(createNewSessionId(exchange));
    }

    private Mono<String> createNewSessionId(ServerWebExchange exchange) {
        String sessionId = UUID.randomUUID().toString();
        exchange.getResponse().getCookies().add(SESSION_COOKIE_NAME,
                org.springframework.http.ResponseCookie.from(SESSION_COOKIE_NAME, sessionId)
                        .maxAge(COOKIE_MAX_AGE)
                        .path("/")
                        .httpOnly(true)
                        .build());
        return Mono.just(sessionId);
    }
}
