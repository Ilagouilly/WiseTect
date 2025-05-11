package com.wisetect.architectureservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for setting up the WebClient used to interact with the
 * LLM API.
 * It retrieves the API URL and key from application properties.
 */
@Configuration
public class LlmConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(LlmConfig.class);

    /**
     * The base URL for the LLM API.
     * Defaults to a pre-configured URL if not provided in the application
     * properties.
     */
    @Value("${wisetect.llm.api-url:https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent}")
    private String llmApiUrl;

    /**
     * The API key for authenticating with the LLM API.
     */
    @Value("${wisetect.llm.api-key}")
    private String llmApiKey;

    /**
     * Creates a WebClient bean configured to interact with the LLM API.
     * Logs the creation of the WebClient for debugging purposes.
     *
     * @return a configured WebClient instance.
     */
    @Bean
    public WebClient llmWebClient() {
        LOGGER.info("Creating WebClient for LLM API with URL: {}", llmApiUrl);
        return WebClient.builder()
                .baseUrl(llmApiUrl + "?key=" + llmApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
