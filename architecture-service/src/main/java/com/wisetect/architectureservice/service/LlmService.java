package com.wisetect.architectureservice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Service
public class LlmService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LlmService.class);

    private final WebClient llmWebClient;
    private final ObjectMapper objectMapper;

    @Value("${wisetect.llm.model:gemini-2.0-flash}")
    private String llmModel;

    @Value("${wisetect.llm.max-tokens:4000}")
    private int maxTokens;

    @Value("${wisetect.llm.api-key}")
    private String llmApiKey;

    public LlmService(WebClient llmWebClient, ObjectMapper objectMapper) {
        this.llmWebClient = llmWebClient;
        this.objectMapper = objectMapper;
    }

    public Mono<Map<String, Object>> generateArchitectureSuggestion(Map<String, Object> requirement) {
        String prompt = buildPrompt(requirement);

        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> systemPart = new HashMap<>();
        systemPart.put("text",
                "You are WiseTect, an expert software architecture assistant. Your task is to analyze user requirements and generate a detailed software architecture recommendation. Your response must be in valid JSON format with two main sections: 'diagram' (containing components and connections) and 'analysis' (containing tradeoffs, strengths, weaknesses, and recommendations).");

        Map<String, Object> userContent = new HashMap<>();
        userContent.put("parts", List.of(part));
        userContent.put("role", "user");

        Map<String, Object> systemContent = new HashMap<>();
        systemContent.put("parts", List.of(systemPart));
        systemContent.put("role", "model");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(systemContent, userContent));

        LOGGER.info("Sending request to Gemini API with prompt: {}", prompt);

        return llmWebClient.post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", llmApiKey).build())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(this::processLlmResponse)
                .doOnNext(response -> LOGGER.info("Processed LLM response"))
                .doOnError(error -> LOGGER.error("Error calling LLM API: {}", error.toString()));
    }

    private Mono<Map<String, Object>> processLlmResponse(Map<String, Object> response) {
        try {
            // Extract content from LLM response
            String content = extractContentFromResponse(response);

            // Parse JSON content
            JsonNode jsonNode = objectMapper.readTree(content);

            // Create result map
            Map<String, Object> result = new HashMap<>();
            result.put("diagram", jsonNode.get("diagram"));
            result.put("analysis", jsonNode.get("analysis"));

            return Mono.just(result);
        } catch (Exception e) {
            LOGGER.error("Failed to process LLM response", e);
            return Mono.error(new RuntimeException("Failed to process LLM response", e));
        }
    }

    private String extractContentFromResponse(Map<String, Object> response) {
        // Extract content from Gemini API response
        try {
            if (response.containsKey("candidates") && response.get("candidates") instanceof List) {
                List<?> candidates = (List<?>) response.get("candidates");
                if (!candidates.isEmpty() && candidates.get(0) instanceof Map) {
                    Map<?, ?> candidate = (Map<?, ?>) candidates.get(0);
                    if (candidate.containsKey("content") && candidate.get("content") instanceof Map) {
                        Map<?, ?> content = (Map<?, ?>) candidate.get("content");
                        if (content.containsKey("parts") && content.get("parts") instanceof List) {
                            List<?> parts = (List<?>) content.get("parts");
                            if (!parts.isEmpty() && parts.get(0) instanceof Map) {
                                Map<?, ?> part = (Map<?, ?>) parts.get(0);
                                if (part.containsKey("text")) {
                                    return (String) part.get("text");
                                }
                            }
                        }
                    }
                }
            }

            throw new RuntimeException("Could not extract content from Gemini API response");
        } catch (Exception e) {
            LOGGER.error("Error extracting content from Gemini API response", e);
            throw new RuntimeException("Error extracting content from Gemini API response", e);
        }
    }

    private String buildPrompt(Map<String, Object> requirement) {
        // Build a prompt based on the user requirement
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append(
                "Please analyze the following software requirements and generate an architecture recommendation in valid JSON format with 'diagram' and 'analysis' sections:\n\n");

        // Add requirement details to the prompt
        requirement.forEach((key, value) -> {
            promptBuilder.append(key).append(": ").append(value).append("\n");
        });

        return promptBuilder.toString();
    }
}
