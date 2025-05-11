package com.wisetect.architectureservice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wisetect.architectureservice.exception.LlmProcessingException;

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

    @Autowired
    public LlmService(WebClient llmWebClient, ObjectMapper objectMapper) {
        this.llmWebClient = llmWebClient;
        this.objectMapper = new ObjectMapper();
    }

    public Mono<Map<String, Object>> generateArchitectureSuggestion(Map<String, Object> requirement) {
        String prompt = buildPrompt(requirement);

        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> systemPart = new HashMap<>();
        systemPart.put("text",
                "You are WiseTect, an expert software architecture assistant. Your task is to analyze user requirements and generate a detailed software architecture recommendation. "
                        + "Your response must be in **valid JSON format** with two main sections: **'diagram'** (containing components and connections) and **'analysis'** (containing tradeoffs, strengths, weaknesses, and recommendations). "
                        + "### **Strict JSON Structure Rules:**\n"
                        + "1. The **'components'** field in the 'diagram' section must be a **map** (key-value pairs) where each key is a unique component identifier (e.g., 'component1', 'component2').\n"
                        + "2. The **'connections'** field in the 'diagram' section must also be a **map**, not an array. Each key must be a unique connection identifier (e.g., 'connection1', 'connection2').\n"
                        + "3. The **'strengths'**, **'weaknesses'**, and **'recommendations'** fields in the 'analysis' section must all be **maps**, not arrays.\n"
                        + "4. Double-check that your response strictly follows this structure before submitting.\n"
                        + "\n### **Example JSON Structure:**\n"
                        + "{\n"
                        + "  \"diagram\": {\n"
                        + "    \"components\": {\n"
                        + "      \"component1\": {\n"
                        + "        \"name\": \"Web Application (Frontend)\",\n"
                        + "        \"type\": \"Client\",\n"
                        + "        \"description\": \"User interface for HR management. Built with a modern web framework (React, Angular, or Vue.js).\",\n"
                        + "        \"technologies\": [\"JavaScript\", \"HTML\", \"CSS\", \"React/Angular/Vue.js\"]\n"
                        + "      },\n"
                        + "      \"component2\": {\n"
                        + "        \"name\": \"API Gateway\",\n"
                        + "        \"type\": \"Server\",\n"
                        + "        \"description\": \"Entry point for all client requests. Handles routing, authentication, authorization, and rate limiting.\",\n"
                        + "        \"technologies\": [\"Node.js (Express/NestJS)\", \"Python (FastAPI/Flask)\", \"Java (Spring Boot)\", \"NGINX\", \"Kong\"]\n"
                        + "      }\n"
                        + "    },\n"
                        + "    \"connections\": {\n"
                        + "      \"connection1\": {\n"
                        + "        \"source\": \"component1\",\n"
                        + "        \"target\": \"component2\",\n"
                        + "        \"type\": \"HTTP/HTTPS\"\n"
                        + "      }\n"
                        + "    }\n"
                        + "  },\n"
                        + "  \"analysis\": {\n"
                        + "    \"strengths\": {\n"
                        + "      \"strength1\": {\n"
                        + "        \"title\": \"Scalability\",\n"
                        + "        \"description\": \"Microservices architecture allows independent scaling of individual components based on demand.\",\n"
                        + "        \"impact\": \"Improves performance and resource efficiency.\"\n"
                        + "      }\n"
                        + "    },\n"
                        + "    \"weaknesses\": {\n"
                        + "      \"weakness1\": {\n"
                        + "        \"title\": \"Operational Complexity\",\n"
                        + "        \"description\": \"Managing multiple microservices increases deployment and monitoring complexity.\",\n"
                        + "        \"impact\": \"Requires robust DevOps practices.\",\n"
                        + "        \"mitigationStrategy\": \"Use Kubernetes for container orchestration.\"\n"
                        + "      }\n"
                        + "    },\n"
                        + "    \"recommendations\": {\n"
                        + "      \"recommendation1\": {\n"
                        + "        \"title\": \"Adopt Kubernetes\",\n"
                        + "        \"description\": \"Using a managed Kubernetes service for microservices deployment.\",\n"
                        + "        \"priority\": \"High\",\n"
                        + "        \"effort\": \"Medium\",\n"
                        + "        \"impact\": \"Improves scalability and reliability.\"\n"
                        + "      }\n"
                        + "    },\n"
                        + "    \"metrics\": {\n"
                        + "      \"metric1\": {\n"
                        + "        \"name\": \"Response Time\",\n"
                        + "        \"value\": \"200ms\",\n"
                        + "        \"unit\": \"milliseconds\",\n"
                        + "        \"description\": \"Average API response time under normal load.\"\n"
                        + "      },\n"
                        + "      \"metric2\": {\n"
                        + "        \"name\": \"Uptime\",\n"
                        + "        \"value\": \"99.9%\",\n"
                        + "        \"unit\": \"percentage\",\n"
                        + "        \"description\": \"Service availability over the last 30 days.\"\n"
                        + "      }\n"
                        + "    }\n"
                        + "  }\n"
                        + "}\n"
                        + "### **Important:**\n"
                        + "- Ensure **all** sections (`components`, `connections`, `strengths`, `weaknesses`, `recommendations`) are **maps (key-value pairs), not arrays**.\n"
                        + "- Validate your response to be a well-formed JSON before submission.");

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

            // Clean and validate the content
            String cleanedContent = cleanJsonContent(content);

            if (!isValidJson(cleanedContent)) {
                LOGGER.error("Invalid JSON content received: {}", cleanedContent);
                return Mono.error(new LlmProcessingException("Invalid JSON format in LLM response"));
            }

            // Parse JSON content
            JsonNode jsonNode = objectMapper.readTree(cleanedContent);

            // Validate required fields
            if (!jsonNode.has("diagram") || !jsonNode.has("analysis")) {
                return Mono.error(new LlmProcessingException("Response missing required fields (diagram or analysis)"));
            }

            // Create result map
            Map<String, Object> result = new HashMap<>();
            result.put("diagram", jsonNode.get("diagram"));
            result.put("analysis", jsonNode.get("analysis"));

            return Mono.just(result);
        } catch (Exception e) {
            LOGGER.error("Failed to process LLM response", e);
            return Mono.error(new LlmProcessingException("Failed to process LLM response", e));
        }
    }

    private String cleanJsonContent(String content) {
        // Remove code block markers if present
        String cleaned = content
                .replaceAll("```json\\s*", "")
                .replaceAll("```\\s*$", "")
                .trim()
                .replaceAll("^`+|`+$", ""); // Remove any standalone backticks

        return cleaned;
    }

    private boolean isValidJson(String jsonString) {
        try {
            objectMapper.readTree(jsonString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String extractContentFromResponse(Map<String, Object> response) {
        LOGGER.info("*** extractContentFromResponse: {}", response.toString());
        try {
            if (response.containsKey("candidates") && response.get("candidates") instanceof List) {
                List<?> candidates = (List<?>) response.get("candidates");
                if (!candidates.isEmpty() && candidates.get(0) instanceof Map) {
                    Map<?, ?> candidate = (Map<?, ?>) candidates.get(0);
                    if (candidate.containsKey("content") && candidate.get("content") instanceof Map) {
                        Map<?, ?> content = (Map<?, ?>) candidate.get("content");
                        if (content.containsKey("parts") && content.get("parts") instanceof List) {
                            LOGGER.info(
                                    "*** content.containsKey(\"parts\") && content.get(\"parts\") instanceof List: {}",
                                    content.toString());
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

            throw new LlmProcessingException("Could not extract content from Gemini API response");
        } catch (Exception e) {
            LOGGER.error("Error extracting content from Gemini API response", e);
            throw new LlmProcessingException("Error extracting content from Gemini API response", e);
        }
    }

    private String buildPrompt(Map<String, Object> requirement) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append(
                "Please analyze the following software requirements and generate an architecture recommendation in valid JSON format with 'diagram' and 'analysis' sections:\n\n");

        requirement.forEach((key, value) -> {
            promptBuilder.append(key).append(": ").append(value).append("\n");
        });

        return promptBuilder.toString();
    }
}
