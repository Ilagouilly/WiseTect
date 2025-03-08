package com.wisetect.architectureservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class LlmConfig {
    @Value("${wisetect.llm.api-url:https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent}")
    private String llmApiUrl;

    @Value("${wisetect.llm.api-key}")
    private String llmApiKey;

    @Bean
    public WebClient llmWebClient() {
        return WebClient.builder()
                .baseUrl(llmApiUrl + "?key=" + llmApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
