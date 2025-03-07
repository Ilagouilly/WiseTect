package com.eventlinkr.userservice.security; // Or com.eventlinkr.userservice.security

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.csrf(csrf -> csrf.disable()) // Disable CSRF protection
                .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll() // Allow all requests (adjust as
                                                                                    // needed)
                ).build();
    }
}